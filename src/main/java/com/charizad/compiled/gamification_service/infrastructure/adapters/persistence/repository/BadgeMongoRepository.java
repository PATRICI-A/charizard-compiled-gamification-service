package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository;

import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.BadgeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BadgeMongoRepository extends MongoRepository<BadgeDocument, String> {
    List<BadgeDocument> findByActiveTrue();
    boolean existsByName(String name);
    java.util.Optional<BadgeDocument> findByName(String name);
}
