package com.resourceSharingPlatform.AI_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class FlashcardResponseDTO {
    private String id;
    private String resourceId;
    private String title;
    private Set<String> tags;
    private List<FlashcardItem> flashcards;
    private LocalDateTime createdAt;

    @Data
    public static class FlashcardItem {
        private List<String> points;
    }
}