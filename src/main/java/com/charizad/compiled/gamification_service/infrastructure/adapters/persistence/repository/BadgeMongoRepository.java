package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository;

import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.BadgeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeMongoRepository extends MongoRepository<BadgeDocument, String> {
    List<BadgeDocument> findByActiveTrue();
    boolean existsByName(String name);
}
