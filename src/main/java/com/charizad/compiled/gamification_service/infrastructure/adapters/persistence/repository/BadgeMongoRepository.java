package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository;

import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.BadgeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface BadgeMongoRepository extends MongoRepository<BadgeDocument, UUID> {
    List<BadgeDocument> findByActiveTrue();
    boolean existsByName(String name);
    java.util.Optional<BadgeDocument> findByName(String name);
}
