package com.resourceSharingPlatform.AI_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "youtube_summaries")
public class YouTubeSummary {
    @Id
    private String id;

    private String videoUrl;
    private String videoTitle;
    private String summary;

    private String transcript;

    private LocalDateTime createdAt;
}
