package com.exelynt.booking.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Reservation() {
    }

    public Reservation(Long id, Resource resource, User user, LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status, BigDecimal totalPrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    public static ReservationBuilder builder() {
        return new ReservationBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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

    public static class ReservationBuilder {
        private Long id;
        private Resource resource;
        private User user;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private ReservationStatus status;
        private BigDecimal totalPrice;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        ReservationBuilder() {
        }

        public ReservationBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ReservationBuilder resource(Resource resource) {
            this.resource = resource;
            return this;
        }

        public ReservationBuilder user(User user) {
            this.user = user;
            return this;
        }

        public ReservationBuilder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public ReservationBuilder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public ReservationBuilder status(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public ReservationBuilder totalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public ReservationBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ReservationBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Reservation build() {
            return new Reservation(id, resource, user, startTime, endTime, status, totalPrice, createdAt, updatedAt);
        }
    }
}
