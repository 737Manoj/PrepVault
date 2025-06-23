package com.resourceSharingPlatform.AI_service.repository;


import com.resourceSharingPlatform.AI_service.model.YouTubeSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YouTubeSummaryRepository extends MongoRepository<YouTubeSummary, String> {
    Optional<YouTubeSummary> findByVideoUrl(String videoUrl);
}