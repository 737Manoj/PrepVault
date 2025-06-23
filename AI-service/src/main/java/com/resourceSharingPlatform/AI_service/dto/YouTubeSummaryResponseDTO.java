package com.resourceSharingPlatform.AI_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class YouTubeSummaryResponseDTO {
    private String id;
    private String videoUrl;
    private String videoTitle;
    private String summary;
    private String transcript;
    private LocalDateTime createdAt;
}
