package com.charizad.compiled.gamification_service.infrastructure.adapters.adapter;

import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.domain.ports.out.MonaRepositoryPort;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.MonaDocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper.MonaDocumentMapper;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.MonaMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MonaRepositoryAdapter implements MonaRepositoryPort {

    private final MonaMongoRepository mongoRepository;
    private final MonaDocumentMapper mapper;

    @Override
    public Mona save(Mona Mona) {
        MonaDocument doc = mapper.toDocument(Mona);
        MonaDocument saved = mongoRepository.save(doc);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Mona> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Mona> findAll() {
        return mongoRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Mona> findAllActive() {
        return mongoRepository.findByActiveTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Mona> findByName(String name) {
        return mongoRepository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return mongoRepository.existsByName(name);
    }
}
