package com.charizad.compiled.gamification_service.domain.ports.out;

import com.charizad.compiled.gamification_service.domain.model.UserGamification;

import java.util.List;
import java.util.Optional;

public interface UserGamificationRepositoryPort {
    UserGamification save(UserGamification userGamification);
    Optional<UserGamification> findByUserId(String userId);
    List<UserGamification> findAllOptedInOrderByWeeklyXpDesc(int limit);
    List<UserGamification> findAllOptedInOrderByWeeklyMonasDesc(int limit);
    List<UserGamification> findAllOptedIn();
    void saveAll(List<UserGamification> users);
    long countAllOptedIn();
    long countOptedInWithMoreMonasThan(int weeklyMonas);
}
