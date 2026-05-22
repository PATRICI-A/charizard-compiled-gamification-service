package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository;

import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.EventCodeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface EventCodeMongoRepository extends MongoRepository<EventCodeDocument, UUID> {
    Optional<EventCodeDocument> findByCode(String code);
}
