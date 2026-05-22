package com.charizad.compiled.gamification_service.domain.ports.out;

import com.charizad.compiled.gamification_service.domain.model.Mona;

import java.util.List;
import java.util.Optional;

public interface MonaRepositoryPort {
    Mona save(Mona Mona);
    Optional<Mona> findById(String id);
    Optional<Mona> findByName(String name);
    List<Mona> findAll();
    List<Mona> findAllActive();
    boolean existsByName(String name);
}
