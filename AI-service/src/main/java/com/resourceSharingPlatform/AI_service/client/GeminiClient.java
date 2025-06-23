package com.resourceSharingPlatform.AI_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class GeminiClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    // Getter for WebClient
    public WebClient getWebClient() {
        return webClient;
    }

    // Getter for apiKey
    public String getApiKey() {
        return apiKey;
    }

    public String generateSummary(String transcriptText) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", "Extract themes, topics, and entities discussed in the video:\n" + transcriptText)
                            ))
                    )
            );

            Map response = webClient.post()
                    .uri("/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map> candidates = (List<Map>) response.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map content = (Map) candidates.get(0).get("content");
                List<Map> parts = (List<Map>) content.get("parts");
                return (String) parts.get(0).get("text");
            } else {
                return "Gemini did not return a summary.";
            }

        } catch (Exception e) {
            return "Error calling Gemini: " + e.getMessage();
        }
    }

    public String generateQuiz(String quizPrompt) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", quizPrompt)
                            ))
                    )
            );

            Map response = webClient.post()
                    .uri("/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map> candidates = (List<Map>) response.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map content = (Map) candidates.get(0).get("content");
                List<Map> parts = (List<Map>) content.get("parts");
                String text = (String) parts.get(0).get("text");
                // Strip Markdown fences (e.g., ```json and ```)
                return text.replaceAll("^```json\\n|\\n```$", "").trim();
            } else {
                return "{\"error\": \"Gemini did not return quiz questions.\"}";
            }

        } catch (Exception e) {
            return "{\"error\": \"Error calling Gemini: " + e.getMessage() + "\"}";
        }
    }

    public String generateFlashcards(String flashcardPrompt) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", flashcardPrompt)
                            ))
                    )
            );

            Map response = webClient.post()
                    .uri("/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map> candidates = (List<Map>) response.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map content = (Map) candidates.get(0).get("content");
                List<Map> parts = (List<Map>) content.get("parts");
                String text = (String) parts.get(0).get("text");
                // Strip Markdown fences (e.g., ```json and ```)
                return text.replaceAll("^```json\\n|\\n```$", "").trim();
            } else {
                return "{\"error\": \"Gemini did not return flashcards.\"}";
            }

        } catch (Exception e) {
            return "{\"error\": \"Error calling Gemini: " + e.getMessage() + "\"}";
        }
    }
}
