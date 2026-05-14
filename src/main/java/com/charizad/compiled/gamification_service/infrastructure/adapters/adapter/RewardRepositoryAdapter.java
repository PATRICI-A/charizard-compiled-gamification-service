package com.charizad.compiled.gamification_service.infrastructure.adapters.adapter;

import com.charizad.compiled.gamification_service.domain.model.Reward;
import com.charizad.compiled.gamification_service.domain.ports.out.RewardRepositoryPort;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper.RewardDocumentMapper;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.RewardMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RewardRepositoryAdapter implements RewardRepositoryPort {

    private final RewardMongoRepository mongoRepository;
    private final RewardDocumentMapper mapper;

    @Override
    public Reward save(Reward reward) {
        return mapper.toDomain(mongoRepository.save(mapper.toDocument(reward)));
    }

    @Override
    public Optional<Reward> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Reward> findAllActive() {
        return mongoRepository.findByActiveTrueOrderByXpThresholdAsc()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Reward> findAll() {
        return mongoRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
