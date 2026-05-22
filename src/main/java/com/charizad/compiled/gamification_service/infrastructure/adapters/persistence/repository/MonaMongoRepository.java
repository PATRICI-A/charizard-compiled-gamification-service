package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository;

import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.MonaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MonaMongoRepository extends MongoRepository<MonaDocument, String> {
    List<MonaDocument> findByActiveTrue();
    boolean existsByName(String name);
    java.util.Optional<MonaDocument> findByName(String name);
}
