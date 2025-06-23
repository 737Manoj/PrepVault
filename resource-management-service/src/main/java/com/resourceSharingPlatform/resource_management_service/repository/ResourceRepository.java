package com.resourceSharingPlatform.resource_management_service.repository;

import com.resourceSharingPlatform.resource_management_service.model.Resource;
import com.resourceSharingPlatform.resource_management_service.model.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends MongoRepository<Resource, String> {
    @Query("{ 'tags': { $in: ?0 }, 'type': ?1 }")
    Page<Resource> findByTagsInAndType(List<String> tags, ResourceType type, Pageable pageable);
    @Query("{ 'tags': { $in: ?0 } }")
    Page<Resource> findByTagsIn(List<String> tags, Pageable pageable);
    Page<Resource> findByType(ResourceType type, Pageable pageable);
    
}

