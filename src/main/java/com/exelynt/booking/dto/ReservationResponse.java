package com.exelynt.booking.dto;

import com.exelynt.booking.domain.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservationResponse {
    private Long id;
    private ResourceResponse resource;
    private UserResponse user;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReservationResponse() {
    }

    public ReservationResponse(Long id, ResourceResponse resource, UserResponse user, LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status, BigDecimal totalPrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.resource = resource;
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ReservationResponseBuilder builder() {
        return new ReservationResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResourceResponse getResource() {
        return resource;
    }

    public void setResource(ResourceResponse resource) {
        this.resource = resource;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class ReservationResponseBuilder {
        private Long id;
        private ResourceResponse resource;
        private UserResponse user;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private ReservationStatus status;
        private BigDecimal totalPrice;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        ReservationResponseBuilder() {
        }

        public ReservationResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ReservationResponseBuilder resource(ResourceResponse resource) {
            this.resource = resource;
            return this;
        }

        public ReservationResponseBuilder user(UserResponse user) {
            this.user = user;
            return this;
        }

        public ReservationResponseBuilder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public ReservationResponseBuilder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public ReservationResponseBuilder status(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public ReservationResponseBuilder totalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public ReservationResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ReservationResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ReservationResponse build() {
            return new ReservationResponse(id, resource, user, startTime, endTime, status, totalPrice, createdAt, updatedAt);
        }
    }
}
