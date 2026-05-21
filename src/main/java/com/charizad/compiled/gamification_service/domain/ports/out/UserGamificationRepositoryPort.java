package com.charizad.compiled.gamification_service.domain.ports.out;

import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;

import java.util.List;
import java.util.Optional;

public interface UserGamificationRepositoryPort {
    UserGamification save(UserGamification userGamification);
    Optional<UserGamification> findByUserId(String userId);
    List<UserGamification> findAllOptedInOrderByWeeklyXpDesc(int limit);
    /** Returns all opted-in users sorted by period monas desc, totalMonas as tiebreaker (RN-13.3.5). */
    List<UserGamification> findAllOptedInRankedFor(RankingType type);
    List<UserGamification> findAllOptedIn();
    void saveAll(List<UserGamification> users);
    long countAllOptedIn();
}
