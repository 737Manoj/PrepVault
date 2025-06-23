package com.resourceSharingPlatform.resource_management_service.dto;

import com.resourceSharingPlatform.resource_management_service.model.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceRequestDTO {
    private String title;
    private String description;
    private ResourceType type;
    private String url;
    private Set<String> tags;
}
