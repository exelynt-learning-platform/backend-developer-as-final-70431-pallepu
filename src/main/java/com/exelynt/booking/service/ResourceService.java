package com.exelynt.booking.service;

import com.exelynt.booking.domain.Resource;
import com.exelynt.booking.dto.ResourceRequest;
import com.exelynt.booking.dto.ResourceResponse;
import com.exelynt.booking.exception.ResourceNotFoundException;
import com.exelynt.booking.repository.ResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Transactional
    public ResourceResponse createResource(ResourceRequest request) {
        Resource resource = Resource.builder()
                .name(request.getName())
                .type(request.getType())
                .description(request.getDescription())
                .capacity(request.getCapacity())
                .location(request.getLocation())
                .pricePerHour(request.getPricePerHour())
                .build();

        Resource saved = resourceRepository.save(resource);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ResourceResponse> getAllResources() {
        return resourceRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResourceResponse getResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + id));
        return mapToResponse(resource);
    }

    @Transactional
    public ResourceResponse updateResource(Long id, ResourceRequest request) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + id));

        resource.setName(request.getName());
        resource.setType(request.getType());
        resource.setDescription(request.getDescription());
        resource.setCapacity(request.getCapacity());
        resource.setLocation(request.getLocation());
        resource.setPricePerHour(request.getPricePerHour());

        Resource updated = resourceRepository.save(resource);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found with ID: " + id);
        }
        resourceRepository.deleteById(id);
    }

    public ResourceResponse mapToResponse(Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .name(resource.getName())
                .type(resource.getType())
                .description(resource.getDescription())
                .capacity(resource.getCapacity())
                .location(resource.getLocation())
                .pricePerHour(resource.getPricePerHour())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();
    }
}
