package com.charizad.compiled.gamification_service.infrastructure.adapters.adapter;

import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.BadgeDocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper.BadgeDocumentMapper;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.BadgeMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BadgeRepositoryAdapter implements BadgeRepositoryPort {

    private final BadgeMongoRepository mongoRepository;
    private final BadgeDocumentMapper mapper;

    @Override
    public Badge save(Badge badge) {
        BadgeDocument doc = mapper.toDocument(badge);
        BadgeDocument saved = mongoRepository.save(doc);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Badge> findById(UUID id) {
        return mongoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Badge> findAll() {
        return mongoRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Badge> findAllActive() {
        return mongoRepository.findByActiveTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Badge> findByName(String name) {
        return mongoRepository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return mongoRepository.existsByName(name);
    }
}
