package com.resourceSharingPlatform.AI_service.dto;

import jakarta.validation.constraints.NotBlank;

public class QuizRequestDTO {

    @NotBlank(message = "Resource ID is required")
    private String resourceId;

    // Getters and setters
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
}