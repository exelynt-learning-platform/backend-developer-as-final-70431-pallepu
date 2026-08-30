package com.exelynt.booking.service;

import com.exelynt.booking.domain.*;
import com.exelynt.booking.dto.*;
import com.exelynt.booking.exception.InvalidReservationException;
import com.exelynt.booking.exception.ResourceNotFoundException;
import com.exelynt.booking.exception.UnauthorizedAccessException;
import com.exelynt.booking.repository.ReservationRepository;
import com.exelynt.booking.repository.ReservationSpecification;
import com.exelynt.booking.repository.ResourceRepository;
import com.exelynt.booking.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final ResourceService resourceService;
    private final AuthService authService;

    public ReservationService(
            ReservationRepository reservationRepository,
            ResourceRepository resourceRepository,
            UserRepository userRepository,
            ResourceService resourceService,
            AuthService authService) {
        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
        this.resourceService = resourceService;
        this.authService = authService;
    }

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));

        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + request.getResourceId()));

        validateReservationTime(request.getStartTime(), request.getEndTime());
        checkForOverlappingReservations(resource.getId(), request.getStartTime(), request.getEndTime(), null);

        BigDecimal totalPrice = calculateTotalPrice(resource.getPricePerHour(), request.getStartTime(), request.getEndTime());

        Reservation reservation = Reservation.builder()
                .resource(resource)
                .user(user)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(ReservationStatus.PENDING)
                .totalPrice(totalPrice)
                .build();

        Reservation saved = reservationRepository.save(reservation);
        return mapToResponse(saved);
    }

    private static final java.util.Set<String> ALLOWED_SORT_FIELDS = java.util.Set.of("id", "createdAt", "startTime", "endTime", "totalPrice", "status");

    @Transactional(readOnly = true)
    public PagedResponse<ReservationResponse> getReservations(
            ReservationStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy,
            String sortDir,
            String currentUserEmail,
            boolean isAdmin
    ) {
        Long userIdFilter = null;
        if (!isAdmin) {
            User user = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserEmail));
            userIdFilter = user.getId();
        }

        String fieldToSort = (sortBy != null && !sortBy.isBlank()) ? sortBy : "createdAt";
        if (!ALLOWED_SORT_FIELDS.contains(fieldToSort)) {
            throw new InvalidReservationException("Invalid sortBy parameter: '" + sortBy + "'. Allowed sort fields are: " + ALLOWED_SORT_FIELDS);
        }

        int validPage = Math.max(0, page);
        int validSize = Math.max(1, Math.min(size, 100));

        Sort sort = "asc".equalsIgnoreCase(sortDir) ? Sort.by(fieldToSort).ascending() : Sort.by(fieldToSort).descending();
        Pageable pageable = PageRequest.of(validPage, validSize, sort);

        Specification<Reservation> spec = ReservationSpecification.filterReservations(
                status, minPrice, maxPrice, userIdFilter
        );

        Page<Reservation> reservationPage = reservationRepository.findAll(spec, pageable);

        List<ReservationResponse> content = reservationPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PagedResponse.<ReservationResponse>builder()
                .content(content)
                .page(reservationPage.getNumber())
                .size(reservationPage.getSize())
                .totalElements(reservationPage.getTotalElements())
                .totalPages(reservationPage.getTotalPages())
                .last(reservationPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id, String currentUserEmail, boolean isAdmin) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));

        boolean isOwner = currentUserEmail != null && currentUserEmail.equalsIgnoreCase(reservation.getUser().getEmail());

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedAccessException("You are not authorized to view this reservation");
        }

        return mapToResponse(reservation);
    }

    @Transactional
    public ReservationResponse updateReservationStatus(
            Long id,
            ReservationStatus newStatus,
            String currentUserEmail,
            boolean isAdmin
    ) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));

        boolean isOwner = currentUserEmail != null && currentUserEmail.equalsIgnoreCase(reservation.getUser().getEmail());

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedAccessException("You are not authorized to modify this reservation");
        }

        if (!isAdmin && isOwner) {
            if (newStatus != ReservationStatus.CANCELLED) {
                throw new InvalidReservationException("Users are only allowed to cancel their reservations");
            }
        }

        reservation.setStatus(newStatus);
        Reservation updated = reservationRepository.save(reservation);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation not found with ID: " + id);
        }
        reservationRepository.deleteById(id);
    }

    private void validateReservationTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new InvalidReservationException("Start time and end time must not be null");
        }

        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new InvalidReservationException("Start time must be strictly before end time");
        }

        if (startTime.isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new InvalidReservationException("Start time cannot be in the past");
        }
    }

    private void checkForOverlappingReservations(Long resourceId, LocalDateTime startTime, LocalDateTime endTime, Long reservationId) {
        List<ReservationStatus> activeStatuses = List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);
        List<Reservation> overlaps;
        if (reservationId == null) {
            overlaps = reservationRepository.findOverlappingReservations(resourceId, startTime, endTime, activeStatuses);
        } else {
            overlaps = reservationRepository.findOverlappingReservationsExcludingId(resourceId, startTime, endTime, activeStatuses, reservationId);
        }

        if (!overlaps.isEmpty()) {
            throw new InvalidReservationException("The resource is already booked during the selected time period");
        }
    }

    private BigDecimal calculateTotalPrice(BigDecimal pricePerHour, LocalDateTime startTime, LocalDateTime endTime) {
        long minutes = Duration.between(startTime, endTime).toMinutes();
        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        return pricePerHour.multiply(hours).setScale(2, RoundingMode.HALF_UP);
    }

    public ReservationResponse mapToResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .resource(resourceService.mapToResponse(reservation.getResource()))
                .user(authService.mapToUserResponse(reservation.getUser()))
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .status(reservation.getStatus())
                .totalPrice(reservation.getTotalPrice())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }
}
