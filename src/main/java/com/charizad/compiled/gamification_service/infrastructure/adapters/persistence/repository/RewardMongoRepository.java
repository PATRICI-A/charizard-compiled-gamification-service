package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository;

import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.RewardDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RewardMongoRepository extends MongoRepository<RewardDocument, String> {

    /** Returns all active rewards sorted by xpThreshold ascending. */
    List<RewardDocument> findByActiveTrueOrderByXpThresholdAsc();
}
