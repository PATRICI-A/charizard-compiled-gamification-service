package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository;

import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.UserGamificationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGamificationMongoRepository extends MongoRepository<UserGamificationDocument, String> {
    Optional<UserGamificationDocument> findByUserId(String userId);
    List<UserGamificationDocument> findByRankingOptInTrueOrderByWeeklyXpDesc();
}
