package com.resourceSharingPlatform.AI_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resourceSharingPlatform.AI_service.client.GeminiClient;
import com.resourceSharingPlatform.AI_service.dto.QuizRequestDTO;
import com.resourceSharingPlatform.AI_service.dto.QuizResponseDTO;
import com.resourceSharingPlatform.AI_service.model.Quiz;
import com.resourceSharingPlatform.AI_service.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizService {

    @Autowired
    private ObjectMapper objectMapper;
    private final QuizRepository quizRepository;
    private final GeminiClient geminiClient;
    private final WebClient webClient;

    @Value("${resource.management.service.url}")
    private String resourceServiceUrl;

    public QuizResponseDTO generateQuiz(QuizRequestDTO request) {
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

        // Generate quizzes for each difficulty
        Map<String, List<Quiz.Question>> quizzes = new HashMap<>();
        for (String difficulty : List.of("easy", "medium", "difficult")) {
            String prompt = String.format(
                    "Generate 4 %s multiple-choice questions based on the topic of these tags: [%s]. " +
                            "Do not require a video transcript or any additional content. " +
                            "Return JSON: {questions: [{text: string, options: [string], correctAnswer: string}]}",
                    difficulty, String.join(", ", tags)
            );

            String quizJson = geminiClient.generateQuiz(prompt);
            System.out.println("Gemini API response for difficulty " + difficulty + ": " + quizJson);
            Map quizData;
            try {
                quizData = parseJson(quizJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse Gemini API response for difficulty: " + difficulty + ". Response: " + quizJson, e);
            }
            List<Map> questionsData = (List<Map>) quizData.get("questions");

            if (questionsData == null || questionsData.isEmpty()) {
                throw new RuntimeException("No questions returned for difficulty: " + difficulty);
            }
            List<Quiz.Question> questions = new ArrayList<>();
            for (Map q : questionsData) {
                questions.add(new Quiz.Question(
                        (String) q.get("text"),
                        (List<String>) q.get("options"),
                        (String) q.get("correctAnswer")
                ));
            }
            quizzes.put(difficulty, questions);
        }

        // Save quiz
        Quiz quiz = Quiz.builder()
                .id(null)
                .resourceId(request.getResourceId())
                .title(title)
                .tags(tags)
                .quizzes(quizzes)
                .createdAt(LocalDateTime.now())
                .build();

        quiz = quizRepository.save(quiz);

        // Map to DTO
        return toResponseDTO(quiz);
    }

    public List<QuizResponseDTO> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        return quizzes.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public QuizResponseDTO getQuizById(String quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));
        return toResponseDTO(quiz);
    }

    private QuizResponseDTO toResponseDTO(Quiz quiz) {
        QuizResponseDTO response = new QuizResponseDTO();
        response.setId(quiz.getId());
        response.setResourceId(quiz.getResourceId());
        response.setTitle(quiz.getTitle());
        response.setTags(quiz.getTags());
        response.setQuizzes(convertQuizzes(quiz.getQuizzes()));
        response.setCreatedAt(quiz.getCreatedAt());
        return response;
    }

    private Map<String, List<QuizResponseDTO.Question>> convertQuizzes(Map<String, List<Quiz.Question>> quizzes) {
        Map<String, List<QuizResponseDTO.Question>> result = new HashMap<>();
        for (Map.Entry<String, List<Quiz.Question>> entry : quizzes.entrySet()) {
            List<QuizResponseDTO.Question> dtoQuestions = new ArrayList<>();
            for (Quiz.Question question : entry.getValue()) {
                QuizResponseDTO.Question dtoQuestion = new QuizResponseDTO.Question();
                dtoQuestion.setText(question.getText());
                dtoQuestion.setOptions(question.getOptions());
                dtoQuestion.setCorrectAnswer(question.getCorrectAnswer());
                dtoQuestions.add(dtoQuestion);
            }
            result.put(entry.getKey(), dtoQuestions);
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

