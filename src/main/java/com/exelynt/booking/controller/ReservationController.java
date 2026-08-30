package com.exelynt.booking.controller;

import com.exelynt.booking.domain.ReservationStatus;
import com.exelynt.booking.dto.*;
import com.exelynt.booking.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/reservations")
@Tag(name = "Reservations", description = "Endpoints for creating, managing, filtering, and cancelling reservations")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @Operation(summary = "Create a reservation (User identity automatically extracted from JWT)")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication) {
        
        String currentUserEmail = authentication.getName();
        ReservationResponse response = reservationService.createReservation(request, currentUserEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get paginated reservations with optional filtering by status and price range (USER sees own, ADMIN sees all)")
    public ResponseEntity<PagedResponse<ReservationResponse>> getReservations(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {

        String currentUserEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        PagedResponse<ReservationResponse> response = reservationService.getReservations(
                status, minPrice, maxPrice, page, size, sortBy, sortDir, currentUserEmail, isAdmin
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation details by ID (USER can view own, ADMIN can view any)")
    public ResponseEntity<ReservationResponse> getReservationById(
            @PathVariable Long id,
            Authentication authentication) {

        String currentUserEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        ReservationResponse response = reservationService.getReservationById(id, currentUserEmail, isAdmin);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update reservation status (ADMIN can set any status; USER can cancel own reservation)")
    public ResponseEntity<ReservationResponse> updateReservationStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReservationStatusUpdateRequest request,
            Authentication authentication) {

        String currentUserEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        ReservationResponse response = reservationService.updateReservationStatus(
                id, request.getStatus(), currentUserEmail, isAdmin
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete reservation by ID (ADMIN only)")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
