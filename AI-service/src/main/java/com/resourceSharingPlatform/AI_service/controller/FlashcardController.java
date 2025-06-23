package com.resourceSharingPlatform.AI_service.controller;

import com.resourceSharingPlatform.AI_service.dto.FlashcardRequestDTO;
import com.resourceSharingPlatform.AI_service.dto.FlashcardResponseDTO;
import com.resourceSharingPlatform.AI_service.service.FlashcardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")

@RequiredArgsConstructor
public class FlashcardController {

    private final FlashcardService flashcardService;

    @PostMapping("/generate-flashcards")
    public ResponseEntity<FlashcardResponseDTO> generateFlashcards(@RequestBody FlashcardRequestDTO request) {
        FlashcardResponseDTO response = flashcardService.generateFlashcards(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/flashcards/resource/{resourceId}")
    public ResponseEntity<FlashcardResponseDTO> getFlashcardByResourceId(@PathVariable String resourceId) {
        FlashcardResponseDTO response = flashcardService.getFlashcardByResourceId(resourceId);
        return ResponseEntity.ok(response);
    }
}
