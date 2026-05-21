package com.charizad.compiled.gamification_service.infrastructure.adapters.adapter;

import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.UserGamificationDocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper.UserGamificationDocumentMapper;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.UserGamificationMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
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
    public List<UserGamification> findAllOptedInOrderByWeeklyXpDesc(int limit) {
        return mongoRepository
                .findByRankingOptInTrueOrderByWeeklyXpDesc(PageRequest.of(0, limit))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<UserGamification> findAllOptedInRankedFor(RankingType type) {
        List<UserGamification> all = mongoRepository.findByRankingOptInTrue().stream()
                .map(mapper::toDomain)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        Comparator<UserGamification> comparator = switch (type) {
            case MONTHLY -> (a, b) -> {
                int cmp = Integer.compare(b.getMonthlyMonas(), a.getMonthlyMonas());
                return cmp != 0 ? cmp : Integer.compare(b.getTotalMonas(), a.getTotalMonas());
            };
            case SEMESTER -> (a, b) -> {
                int cmp = Integer.compare(b.getSemestralMonas(), a.getSemestralMonas());
                return cmp != 0 ? cmp : Integer.compare(b.getTotalMonas(), a.getTotalMonas());
            };
            default -> (a, b) -> {
                int cmp = Integer.compare(b.getWeeklyMonas(), a.getWeeklyMonas());
                return cmp != 0 ? cmp : Integer.compare(b.getTotalMonas(), a.getTotalMonas());
            };
        };

        all.sort(comparator);
        return all;
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
}
