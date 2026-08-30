package com.exelynt.booking.dto;

import com.exelynt.booking.domain.ReservationStatus;
import jakarta.validation.constraints.NotNull;

public class ReservationStatusUpdateRequest {

    @NotNull(message = "Reservation status is required")
    private ReservationStatus status;

    public ReservationStatusUpdateRequest() {
    }

    public ReservationStatusUpdateRequest(ReservationStatus status) {
        this.status = status;
    }

    public static ReservationStatusUpdateRequestBuilder builder() {
        return new ReservationStatusUpdateRequestBuilder();
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public static class ReservationStatusUpdateRequestBuilder {
        private ReservationStatus status;

        ReservationStatusUpdateRequestBuilder() {
        }

        public ReservationStatusUpdateRequestBuilder status(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public ReservationStatusUpdateRequest build() {
            return new ReservationStatusUpdateRequest(status);
        }
    }
}
