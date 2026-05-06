package com.charizad.compiled.gamification_service.domain.ports.out;

import com.charizad.compiled.gamification_service.domain.model.Badge;

import java.util.List;
import java.util.Optional;

public interface BadgeRepositoryPort {
    Badge save(Badge badge);
    Optional<Badge> findById(String id);
    List<Badge> findAll();
    List<Badge> findAllActive();
    boolean existsByName(String name);
}
