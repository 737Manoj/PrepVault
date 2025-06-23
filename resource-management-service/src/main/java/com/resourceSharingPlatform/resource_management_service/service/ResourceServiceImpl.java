package com.resourceSharingPlatform.resource_management_service.service;

import com.resourceSharingPlatform.resource_management_service.dto.ResourceRequestDTO;
import com.resourceSharingPlatform.resource_management_service.dto.ResourceResponseDTO;
import com.resourceSharingPlatform.resource_management_service.model.Resource;
import com.resourceSharingPlatform.resource_management_service.model.ResourceType;
import com.resourceSharingPlatform.resource_management_service.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    @Override
    public ResourceResponseDTO createResource(ResourceRequestDTO requestDTO) {
        Resource resource = Resource.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .type(requestDTO.getType())
                .url(requestDTO.getUrl())
                .tags(requestDTO.getTags())
                .build();

        Resource saved = resourceRepository.save(resource);

        return mapToDTO(saved);
    }

    @Override
    public List<ResourceResponseDTO> getAllResources() {
        return resourceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ResourceResponseDTO getResourceById(String id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
        return mapToDTO(resource);
    }

    @Override
    public List<ResourceResponseDTO> searchResources(String tags, String type, int page) {
        try {
            // Normalize tags
            List<String> tagList = tags == null || tags.trim().isEmpty() ?
                    List.of() :
                    Arrays.stream(tags.split(","))
                            .map(String::trim)
                            .map(String::toLowerCase)
                            .filter(tag -> !tag.isEmpty())
                            .distinct()
                            .collect(Collectors.toList());

            System.out.println("Searching with tags: " + tagList + ", type: " + type + ", page: " + page);

            // Parse type
            ResourceType resourceType = null;
            if (type != null && !type.trim().isEmpty()) {
                try {
                    resourceType = ResourceType.valueOf(type.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid resource type: " + type);
                    throw new IllegalArgumentException("Invalid resource type: " + type);
                }
            }

            // Create pageable (size 5, sort by createdAt descending)
            Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

            // Query repository
            Page<Resource> resourcePage;
            if (!tagList.isEmpty() && resourceType != null) {
                resourcePage = resourceRepository.findByTagsInAndType(tagList, resourceType, pageable);
            } else if (!tagList.isEmpty()) {
                resourcePage = resourceRepository.findByTagsIn(tagList, pageable);
            } else if (resourceType != null) {
                resourcePage = resourceRepository.findByType(resourceType, pageable);
            } else {
                resourcePage = resourceRepository.findAll(pageable);
            }

            // Log results
            System.out.println("Found " + resourcePage.getContent().size() + " resources: " + resourcePage.getContent());

            // Map to DTO
            return resourcePage.getContent().stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error searching resources: " + e.getMessage());
            throw new RuntimeException("Failed to search resources", e);
        }
    }




    @Override
    public void deleteResource(String id) {
        resourceRepository.deleteById(id);
    }

    private ResourceResponseDTO mapToDTO(Resource resource) {
        return ResourceResponseDTO.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .description(resource.getDescription())
                .type(resource.getType())
                .url(resource.getUrl())
                .tags(resource.getTags())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();
    }
}
