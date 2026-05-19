package com.charizad.compiled.gamification_service.domain.ports.out;

import com.charizad.compiled.gamification_service.domain.model.EventCode;

import java.util.Optional;

public interface EventCodeRepositoryPort {
    Optional<EventCode> findByCode(String code);
    EventCode save(EventCode eventCode);
}
