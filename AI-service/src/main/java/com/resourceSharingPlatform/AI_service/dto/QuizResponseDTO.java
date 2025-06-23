package com.resourceSharingPlatform.AI_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResponseDTO {

    private String id;

    private String resourceId;

    private String title;
    private Set<String> tags;

    private Map<String, List<Question>> quizzes; // Key: "easy", "medium", "difficult"

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
