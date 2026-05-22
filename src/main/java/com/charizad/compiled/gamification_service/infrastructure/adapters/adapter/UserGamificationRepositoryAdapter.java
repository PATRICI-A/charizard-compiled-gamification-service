package com.charizad.compiled.gamification_service.infrastructure.adapters.adapter;

import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.UserGamificationDocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper.UserGamificationDocumentMapper;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.UserGamificationMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserGamificationRepositoryAdapter implements UserGamificationRepositoryPort {

    private final UserGamificationMongoRepository mongoRepository;
    private final UserGamificationDocumentMapper mapper;

    @Override
    public UserGamification save(UserGamification user) {
        UserGamificationDocument doc = mapper.toDocument(user);
        UserGamificationDocument saved = mongoRepository.save(doc);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserGamification> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public List<UserGamification> findAllOptedInOrderByWeeklyMonasDesc(int limit) {
        return mongoRepository
                .findByRankingOptInTrueOrderByWeeklyMonasDesc(PageRequest.of(0, limit))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<UserGamification> findAllOptedInOrderByMonthlyMonasDesc(int limit) {
        return mongoRepository
                .findByRankingOptInTrueOrderByMonthlyMonasDesc(PageRequest.of(0, limit))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<UserGamification> findAllOptedInOrderBySemesterMonasDesc(int limit) {
        return mongoRepository
                .findByRankingOptInTrueOrderBySemesterMonasDesc(PageRequest.of(0, limit))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<UserGamification> findAllOptedIn() {
        return mongoRepository.findByRankingOptInTrue().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void saveAll(List<UserGamification> users) {
        List<UserGamificationDocument> docs = users.stream()
                .map(mapper::toDocument)
                .toList();
        mongoRepository.saveAll(docs);
    }

    @Override
    public long countAllOptedIn() {
        return mongoRepository.countByRankingOptInTrue();
    }

    @Override
    public long countOptedInWithMoreWeeklyMonasThan(int weeklyMonas) {
        return mongoRepository.countByRankingOptInTrueAndWeeklyMonasGreaterThan(weeklyMonas);
    }

    @Override
    public long countOptedInWithMoreMonthlyMonasThan(int monthlyMonas) {
        return mongoRepository.countByRankingOptInTrueAndMonthlyMonasGreaterThan(monthlyMonas);
    }

    @Override
    public long countOptedInWithMoreSemesterMonasThan(int semesterMonas) {
        return mongoRepository.countByRankingOptInTrueAndSemesterMonasGreaterThan(semesterMonas);
    }
}
