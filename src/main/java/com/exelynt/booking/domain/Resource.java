package com.exelynt.booking.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;

    @Column(length = 1000)
    private String description;

    private Integer capacity;

    private String location;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerHour;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Resource() {
    }

    public Resource(Long id, String name, ResourceType type, String description, Integer capacity, String location, BigDecimal pricePerHour, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.capacity = capacity;
        this.location = location;
        this.pricePerHour = pricePerHour;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ResourceBuilder builder() {
        return new ResourceBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(BigDecimal pricePerHour) {
        this.pricePerHour = pricePerHour;
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

    public static class ResourceBuilder {
        private Long id;
        private String name;
        private ResourceType type;
        private String description;
        private Integer capacity;
        private String location;
        private BigDecimal pricePerHour;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        ResourceBuilder() {
        }

        public ResourceBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ResourceBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ResourceBuilder type(ResourceType type) {
            this.type = type;
            return this;
        }

        public ResourceBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ResourceBuilder capacity(Integer capacity) {
            this.capacity = capacity;
            return this;
        }

        public ResourceBuilder location(String location) {
            this.location = location;
            return this;
        }

        public ResourceBuilder pricePerHour(BigDecimal pricePerHour) {
            this.pricePerHour = pricePerHour;
            return this;
        }

        public ResourceBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ResourceBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Resource build() {
            return new Resource(id, name, type, description, capacity, location, pricePerHour, createdAt, updatedAt);
        }
    }
}
