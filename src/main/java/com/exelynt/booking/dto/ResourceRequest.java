package com.exelynt.booking.dto;

import com.exelynt.booking.domain.ResourceType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ResourceRequest {

    @NotBlank(message = "Resource name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Resource type is required")
    private ResourceType type;

    private String description;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private String location;

    @NotNull(message = "Price per hour is required")
    @DecimalMin(value = "0.00", inclusive = false, message = "Price per hour must be greater than 0")
    private BigDecimal pricePerHour;

    public ResourceRequest() {
    }

    public ResourceRequest(String name, ResourceType type, String description, Integer capacity, String location, BigDecimal pricePerHour) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.capacity = capacity;
        this.location = location;
        this.pricePerHour = pricePerHour;
    }

    public static ResourceRequestBuilder builder() {
        return new ResourceRequestBuilder();
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

    public static class ResourceRequestBuilder {
        private String name;
        private ResourceType type;
        private String description;
        private Integer capacity;
        private String location;
        private BigDecimal pricePerHour;

        ResourceRequestBuilder() {
        }

        public ResourceRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ResourceRequestBuilder type(ResourceType type) {
            this.type = type;
            return this;
        }

        public ResourceRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ResourceRequestBuilder capacity(Integer capacity) {
            this.capacity = capacity;
            return this;
        }

        public ResourceRequestBuilder location(String location) {
            this.location = location;
            return this;
        }

        public ResourceRequestBuilder pricePerHour(BigDecimal pricePerHour) {
            this.pricePerHour = pricePerHour;
            return this;
        }

        public ResourceRequest build() {
            return new ResourceRequest(name, type, description, capacity, location, pricePerHour);
        }
    }
}
