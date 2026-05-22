package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository;

import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.UserGamificationDocument;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserGamificationMongoRepository extends MongoRepository<UserGamificationDocument, String> {
    Optional<UserGamificationDocument> findByUserId(String userId);
    List<UserGamificationDocument> findByRankingOptInTrue();
    List<UserGamificationDocument> findByRankingOptInTrueOrderByWeeklyMonasDesc(PageRequest pageRequest);
    List<UserGamificationDocument> findByRankingOptInTrueOrderByMonthlyMonasDesc(PageRequest pageRequest);
    List<UserGamificationDocument> findByRankingOptInTrueOrderBySemesterMonasDesc(PageRequest pageRequest);
    long countByRankingOptInTrue();
    long countByRankingOptInTrueAndWeeklyMonasGreaterThan(int weeklyMonas);
    long countByRankingOptInTrueAndMonthlyMonasGreaterThan(int monthlyMonas);
    long countByRankingOptInTrueAndSemesterMonasGreaterThan(int semesterMonas);
}
