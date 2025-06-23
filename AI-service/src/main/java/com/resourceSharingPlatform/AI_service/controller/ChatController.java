package com.resourceSharingPlatform.AI_service.controller;

import com.resourceSharingPlatform.AI_service.dto.ChatRequestDTO;
import com.resourceSharingPlatform.AI_service.dto.ChatResponseDTO;
import com.resourceSharingPlatform.AI_service.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/chat")
@RequiredArgsConstructor

public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponseDTO> handleChat(@RequestBody ChatRequestDTO request) {
        try {
            String response = chatService.getChatResponse(request.getMessage());
            return ResponseEntity.ok(new ChatResponseDTO(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ChatResponseDTO("Invalid request: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChatResponseDTO("Server error: " + e.getMessage()));
        }
    }
}
