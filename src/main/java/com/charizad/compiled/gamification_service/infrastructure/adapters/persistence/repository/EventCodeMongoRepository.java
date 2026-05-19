package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository;

import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.EventCodeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EventCodeMongoRepository extends MongoRepository<EventCodeDocument, String> {
    Optional<EventCodeDocument> findByCode(String code);
}
