package com.resourceSharingPlatform.AI_service.repository;

import com.resourceSharingPlatform.AI_service.model.Flashcard;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FlashcardRepository extends MongoRepository<Flashcard, String> {
    Optional<Flashcard> findByResourceId(String resourceId);
}
