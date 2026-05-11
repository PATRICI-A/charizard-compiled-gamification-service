package com.charizad.compiled.gamification_service.domain.ports.out;

import com.charizad.compiled.gamification_service.domain.model.Reward;

import java.util.List;
import java.util.Optional;

public interface RewardRepositoryPort {

    Reward save(Reward reward);

    Optional<Reward> findById(String id);

    /** Returns all active rewards, ordered by xpThreshold ascending. */
    List<Reward> findAllActive();

    /** Returns every reward (active and inactive), for admin listing. */
    List<Reward> findAll();
}
