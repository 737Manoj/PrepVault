package com.resourceSharingPlatform.AI_service.service;


import com.resourceSharingPlatform.AI_service.client.GeminiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final GeminiClient geminiClient;

    public String getChatResponse(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        try {
            // Customize prompt
            String prompt = "You are a helpful chatbot for a resource-sharing platform. Provide concise, accurate answers. Suggest resource types (e.g., VIDEO, ARTICLE) or tags (e.g., networking, fullstack) when relevant. User message: " + userMessage;

            // Call GeminiClient (mimicking generateSummary)
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            Map response = geminiClient.getWebClient()
                    .post()
                    .uri("/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiClient.getApiKey())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map> candidates = (List<Map>) response.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map content = (Map) candidates.get(0).get("content");
                List<Map> parts = (List<Map>) content.get("parts");
                String botResponse = (String) parts.get(0).get("text");

                System.out.println("Chat response generated for message: " + userMessage);
                return botResponse;
            } else {
                throw new RuntimeException("Gemini did not return a response.");
            }
        } catch (Exception e) {
            System.err.println("Error generating chat response: " + e.getMessage());
            throw new RuntimeException("Failed to generate chat response", e);
        }
    }
}