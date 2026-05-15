package com.charizad.compiled.gamification_service.infrastructure.adapters.adapter;

import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.UserGamificationDocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper.UserGamificationDocumentMapper;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.UserGamificationMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserGamificationRepositoryAdapter implements UserGamificationRepositoryPort {

    private final UserGamificationMongoRepository mongoRepository;
    private final UserGamificationDocumentMapper mapper;

    @Override
    public UserGamification save(UserGamification userGamification) {
        UserGamificationDocument doc = mapper.toDocument(userGamification);
        UserGamificationDocument saved = mongoRepository.save(doc);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserGamification> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public List<UserGamification> findAllOptedInOrderByWeeklyXpDesc(int limit) {
        return mongoRepository.findByRankingOptInTrueOrderByWeeklyXpDesc().stream()
                .limit(limit)
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserGamification> findAllOptedIn() {
        return mongoRepository.findByRankingOptInTrueOrderByWeeklyXpDesc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void saveAll(List<UserGamification> users) {
        List<UserGamificationDocument> docs = users.stream()
                .map(mapper::toDocument)
                .collect(Collectors.toList());
        mongoRepository.saveAll(docs);
    }
}
