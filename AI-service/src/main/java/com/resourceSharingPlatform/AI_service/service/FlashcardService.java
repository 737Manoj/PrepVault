package com.resourceSharingPlatform.AI_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resourceSharingPlatform.AI_service.client.GeminiClient;
import com.resourceSharingPlatform.AI_service.dto.FlashcardRequestDTO;
import com.resourceSharingPlatform.AI_service.dto.FlashcardResponseDTO;
import com.resourceSharingPlatform.AI_service.model.Flashcard;
import com.resourceSharingPlatform.AI_service.repository.FlashcardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final GeminiClient geminiClient;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${resource.management.service.url}")
    private String resourceServiceUrl;

    public FlashcardResponseDTO generateFlashcards(FlashcardRequestDTO request) {
        // Fetch resource data
        Map resourceResponse = webClient.get()
                .uri("/api/resources/{resourceId}", request.getResourceId())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resourceResponse == null) {
            throw new RuntimeException("Resource not found: " + request.getResourceId());
        }

        // Extract title and tags
        String title = (String) resourceResponse.get("title");
        if (title == null) {
            throw new RuntimeException("No title found for resource: " + request.getResourceId());
        }
        Object tagsObj = resourceResponse.get("tags");
        Set<String> tags;
        if (tagsObj instanceof List) {
            tags = new HashSet<>((List<String>) tagsObj);
        } else if (tagsObj instanceof Set) {
            tags = (Set<String>) tagsObj;
        } else {
            throw new RuntimeException("Invalid tags format for resource: " + request.getResourceId());
        }
        if (tags.isEmpty()) {
            throw new RuntimeException("No tags found for resource: " + request.getResourceId());
        }

        // Generate flashcards
        String prompt = String.format(
                "Generate 5 flashcards summarizing key concepts for tags: [%s]. " +
                        "Each flashcard has 3 bullet points, each max 12 words. " +
                        "Do not require a video transcript. " +
                        "Return JSON: {flashcards: [{points: [string]}]}",
                String.join(", ", tags)
        );

        String flashcardJson = geminiClient.generateFlashcards(prompt);
        System.out.println("Gemini API response for flashcards: " + flashcardJson);
        Map flashcardData;
        try {
            flashcardData = parseJson(flashcardJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse Gemini API response for flashcards. Response: " + flashcardJson, e);
        }

        List<Map> flashcardsData = (List<Map>) flashcardData.get("flashcards");
        if (flashcardsData == null || flashcardsData.size() != 5) {
            throw new RuntimeException("Invalid or insufficient flashcards returned. Response: " + flashcardJson);
        }

        List<Flashcard.FlashcardItem> flashcards = new ArrayList<>();
        for (Map f : flashcardsData) {
            List<String> points = (List<String>) f.get("points");
            if (points == null || points.size() != 3) {
                throw new RuntimeException("Flashcard must have exactly 3 bullet points. Response: " + flashcardJson);
            }
            flashcards.add(Flashcard.FlashcardItem.builder()
                    .points(points)
                    .build());
        }

        // Save flashcards
        Flashcard flashcard = Flashcard.builder()
                .id(null)
                .resourceId(request.getResourceId())
                .title(title)
                .tags(tags)
                .flashcards(flashcards)
                .createdAt(LocalDateTime.now())
                .build();

        flashcard = flashcardRepository.save(flashcard);

        // Map to DTO
        return toResponseDTO(flashcard);
    }

    public FlashcardResponseDTO getFlashcardByResourceId(String resourceId) {
        Flashcard flashcard = flashcardRepository.findByResourceId(resourceId)
                .orElseThrow(() -> new RuntimeException("Flashcard not found for resourceId: " + resourceId));
        return toResponseDTO(flashcard);
    }

    private FlashcardResponseDTO toResponseDTO(Flashcard flashcard) {
        FlashcardResponseDTO response = new FlashcardResponseDTO();
        response.setId(flashcard.getId());
        response.setResourceId(flashcard.getResourceId());
        response.setTitle(flashcard.getTitle());
        response.setTags(flashcard.getTags());
        response.setFlashcards(convertFlashcards(flashcard.getFlashcards()));
        response.setCreatedAt(flashcard.getCreatedAt());
        return response;
    }

    private List<FlashcardResponseDTO.FlashcardItem> convertFlashcards(List<Flashcard.FlashcardItem> flashcards) {
        List<FlashcardResponseDTO.FlashcardItem> result = new ArrayList<>();
        for (Flashcard.FlashcardItem flashcard : flashcards) {
            FlashcardResponseDTO.FlashcardItem dtoFlashcard = new FlashcardResponseDTO.FlashcardItem();
            dtoFlashcard.setPoints(flashcard.getPoints());
            result.add(dtoFlashcard);
        }
        return result;
    }

    private Map parseJson(String json) throws JsonProcessingException {
        if (json == null || json.trim().isEmpty()) {
            throw new JsonProcessingException("Empty or null JSON response from Gemini API") {};
        }
        return objectMapper.readValue(json, Map.class);
    }
}
