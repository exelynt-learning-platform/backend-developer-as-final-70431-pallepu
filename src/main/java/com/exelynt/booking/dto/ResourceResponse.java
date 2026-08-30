package com.exelynt.booking.dto;

import com.exelynt.booking.domain.ResourceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ResourceResponse {
    private Long id;
    private String name;
    private ResourceType type;
    private String description;
    private Integer capacity;
    private String location;
    private BigDecimal pricePerHour;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ResourceResponse() {
    }

    public ResourceResponse(Long id, String name, ResourceType type, String description, Integer capacity, String location, BigDecimal pricePerHour, LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    public static ResourceResponseBuilder builder() {
        return new ResourceResponseBuilder();
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

    public static class ResourceResponseBuilder {
        private Long id;
        private String name;
        private ResourceType type;
        private String description;
        private Integer capacity;
        private String location;
        private BigDecimal pricePerHour;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        ResourceResponseBuilder() {
        }

        public ResourceResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ResourceResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ResourceResponseBuilder type(ResourceType type) {
            this.type = type;
            return this;
        }

        public ResourceResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ResourceResponseBuilder capacity(Integer capacity) {
            this.capacity = capacity;
            return this;
        }

        public ResourceResponseBuilder location(String location) {
            this.location = location;
            return this;
        }

        public ResourceResponseBuilder pricePerHour(BigDecimal pricePerHour) {
            this.pricePerHour = pricePerHour;
            return this;
        }

        public ResourceResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ResourceResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ResourceResponse build() {
            return new ResourceResponse(id, name, type, description, capacity, location, pricePerHour, createdAt, updatedAt);
        }
    }
}
