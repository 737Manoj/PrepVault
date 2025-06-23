package com.resourceSharingPlatform.AI_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Document(collection = "quizzes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Quiz {

    @Id
    private String id;

    private String resourceId;

    private String title;
    private Set<String> tags;

    private Map<String, List<Question>> quizzes; // Key: "easy", "medium", "difficult"

    @CreatedDate
    private LocalDateTime createdAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Question {
        private String text;
        private List<String> options;
        private String correctAnswer;
    }
}
