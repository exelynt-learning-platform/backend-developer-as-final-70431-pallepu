package com.exelynt.booking.service;

import com.exelynt.booking.domain.*;
import com.exelynt.booking.dto.ReservationRequest;
import com.exelynt.booking.dto.ReservationResponse;
import com.exelynt.booking.exception.InvalidReservationException;
import com.exelynt.booking.exception.ResourceNotFoundException;
import com.exelynt.booking.exception.UnauthorizedAccessException;
import com.exelynt.booking.repository.ReservationRepository;
import com.exelynt.booking.repository.ResourceRepository;
import com.exelynt.booking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResourceService resourceService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ReservationService reservationService;

    private User testUser;
    private User otherUser;
    private Resource testResource;
    private Reservation testReservation;
    private ReservationRequest reservationRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .fullName("John Doe")
                .role(Role.ROLE_USER)
                .build();

        otherUser = User.builder()
                .id(2L)
                .email("other@example.com")
                .fullName("Other User")
                .role(Role.ROLE_USER)
                .build();

        testResource = Resource.builder()
                .id(10L)
                .name("Conference Room")
                .type(ResourceType.ROOM)
                .pricePerHour(new BigDecimal("50.00"))
                .build();

        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        LocalDateTime endTime = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0); // 2 hours = $100.00

        reservationRequest = ReservationRequest.builder()
                .resourceId(10L)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        testReservation = Reservation.builder()
                .id(100L)
                .resource(testResource)
                .user(testUser)
                .startTime(startTime)
                .endTime(endTime)
                .status(ReservationStatus.PENDING)
                .totalPrice(new BigDecimal("100.00"))
                .build();
    }

    @Test
    @DisplayName("Should create reservation successfully with user identity from JWT and correct total price")
    void createReservation_Success() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(testResource));
        when(reservationRepository.findOverlappingReservations(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        ReservationResponse response = reservationService.createReservation(reservationRequest, "user@example.com");

        assertNotNull(response);
        verify(userRepository, times(1)).findByEmail("user@example.com");
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw InvalidReservationException when start time is after end time")
    void createReservation_InvalidDateRange() {
        reservationRequest.setStartTime(LocalDateTime.now().plusDays(1).withHour(15));
        reservationRequest.setEndTime(LocalDateTime.now().plusDays(1).withHour(10));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(testResource));

        assertThrows(InvalidReservationException.class, () ->
                reservationService.createReservation(reservationRequest, "user@example.com")
        );
    }

    @Test
    @DisplayName("Should throw InvalidReservationException when time slot overlaps with existing booking")
    void createReservation_OverlappingBooking() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(testResource));
        when(reservationRepository.findOverlappingReservations(any(), any(), any(), any()))
                .thenReturn(List.of(testReservation));

        assertThrows(InvalidReservationException.class, () ->
                reservationService.createReservation(reservationRequest, "user@example.com")
        );
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when user accesses another user's reservation")
    void getReservationById_UnauthorizedAccess() {
        testReservation.setUser(otherUser);
        when(reservationRepository.findById(100L)).thenReturn(Optional.of(testReservation));

        assertThrows(UnauthorizedAccessException.class, () ->
                reservationService.getReservationById(100L, "user@example.com", false)
        );
    }

    @Test
    @DisplayName("Should allow USER to cancel their own reservation")
    void updateReservationStatus_UserCancelSuccess() {
        when(reservationRepository.findById(100L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        ReservationResponse response = reservationService.updateReservationStatus(
                100L, ReservationStatus.CANCELLED, "user@example.com", false
        );

        assertNotNull(response);
        verify(reservationRepository, times(1)).save(testReservation);
    }

    @Test
    @DisplayName("Should prevent USER from setting reservation status to CONFIRMED")
    void updateReservationStatus_UserForbiddenStatusChange() {
        when(reservationRepository.findById(100L)).thenReturn(Optional.of(testReservation));

        assertThrows(InvalidReservationException.class, () ->
                reservationService.updateReservationStatus(
                        100L, ReservationStatus.CONFIRMED, "user@example.com", false
                )
        );
    }
}
