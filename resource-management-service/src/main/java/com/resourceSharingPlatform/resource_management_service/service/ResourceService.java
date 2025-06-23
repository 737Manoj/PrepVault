package com.resourceSharingPlatform.resource_management_service.service;

import com.resourceSharingPlatform.resource_management_service.dto.ResourceRequestDTO;
import com.resourceSharingPlatform.resource_management_service.dto.ResourceResponseDTO;

import java.util.List;

public interface ResourceService {

    ResourceResponseDTO createResource(ResourceRequestDTO requestDTO);

    List<ResourceResponseDTO> getAllResources();

    ResourceResponseDTO getResourceById(String id);

    void deleteResource(String id);

    List<ResourceResponseDTO> searchResources(String tags, String type, int page);
}

