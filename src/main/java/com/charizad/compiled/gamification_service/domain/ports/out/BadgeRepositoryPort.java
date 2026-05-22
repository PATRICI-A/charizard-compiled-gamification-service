package com.charizad.compiled.gamification_service.domain.ports.out;

import com.charizad.compiled.gamification_service.domain.model.Badge;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BadgeRepositoryPort {
    Badge save(Badge badge);
    Optional<Badge> findById(UUID id);
    Optional<Badge> findByName(String name);
    List<Badge> findAll();
    List<Badge> findAllActive();
    boolean existsByName(String name);
}
