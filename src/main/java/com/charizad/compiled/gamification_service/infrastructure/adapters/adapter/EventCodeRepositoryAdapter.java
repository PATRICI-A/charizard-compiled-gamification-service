package com.charizad.compiled.gamification_service.infrastructure.adapters.adapter;

import com.charizad.compiled.gamification_service.domain.model.EventCode;
import com.charizad.compiled.gamification_service.domain.ports.out.EventCodeRepositoryPort;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper.EventCodeDocumentMapper;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.EventCodeMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventCodeRepositoryAdapter implements EventCodeRepositoryPort {

    private final EventCodeMongoRepository mongoRepository;
    private final EventCodeDocumentMapper mapper;

    @Override
    public Optional<EventCode> findByCode(String code) {
        return mongoRepository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public EventCode save(EventCode eventCode) {
        return mapper.toDomain(mongoRepository.save(mapper.toDocument(eventCode)));
    }
}
