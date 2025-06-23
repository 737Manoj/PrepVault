package com.resourceSharingPlatform.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private String id;

    private String username;

    private String email;

    private Set<String> roles;

    private Instant createdAt;

    private Instant updatedAt;
}
