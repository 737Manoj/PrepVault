package com.resourceSharingPlatform.AI_service.repository;
import com.resourceSharingPlatform.AI_service.model.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface QuizRepository extends MongoRepository<Quiz, String> {}
