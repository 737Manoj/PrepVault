package com.resourceSharingPlatform.AI_service.controller;

import com.resourceSharingPlatform.AI_service.dto.QuizRequestDTO;
import com.resourceSharingPlatform.AI_service.dto.QuizResponseDTO;
import com.resourceSharingPlatform.AI_service.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/api")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/generate-quiz")
    @ResponseStatus(HttpStatus.CREATED)
    public QuizResponseDTO generateQuiz(@Valid @RequestBody QuizRequestDTO request) {
        return quizService.generateQuiz(request);
    }

    @GetMapping("/quizzes")
    public List<QuizResponseDTO> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }


    @GetMapping("/quizzes/{quizId}")
    public QuizResponseDTO getQuizById(@PathVariable String quizId) {
        return quizService.getQuizById(quizId);
    }
}
