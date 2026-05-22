package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.MonaDetailResponse;
import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.GetMonasUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.MonaRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.MonaProgress;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedMona;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetMonasService implements GetMonasUseCase {

    private final MonaRepositoryPort MonaRepository;
    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public List<MonaDetailResponse> execute(String userId) {
        List<Mona> catalog = MonaRepository.findAllActive();
        Optional<UserGamification> userOpt = userGamificationRepository.findByUserId(userId);

        return catalog.stream()
                .map(Mona -> buildMonaDetailResponse(Mona, userOpt.orElse(null)))
                .toList();
    }

    static MonaDetailResponse buildMonaDetailResponse(Mona Mona, UserGamification user) {
        EarnedMona earned = user == null ? null : user.getEarnedMonas().stream()
                .filter(b -> b.getMonaId().equals(Mona.getId()))
                .findFirst().orElse(null);

        MonaProgress progress = user == null ? null : user.getProgress().stream()
                .filter(p -> p.getMonaId().equals(Mona.getId()))
                .findFirst().orElse(null);

        if (earned != null) {
            int target = progress != null ? progress.getRequiredValue() : 1;
            return MonaDetailResponse.builder()
                    .monaId(Mona.getId())
                    .name(Mona.getName())
                    .description(Mona.getDescription())
                    .rarity(Mona.getCategory())
                    .unlocked(true)
                    .earnedAt(earned.getEarnedAt().toLocalDate())
                    .currentCount(target)
                    .targetCount(target)
                    .progressPercentage(100.0f)
                    .build();
        }

        int current = progress != null ? progress.getCurrentValue() : 0;
        int target = progress != null ? progress.getRequiredValue() : 1;
        float pct = target > 0 ? Math.min(current * 100.0f / target, 100.0f) : 0.0f;

        return MonaDetailResponse.builder()
                .monaId(Mona.getId())
                .name(Mona.getName())
                .description(Mona.getDescription())
                .rarity(Mona.getCategory())
                .unlocked(false)
                .earnedAt(null)
                .currentCount(current)
                .targetCount(target)
                .progressPercentage(pct)
                .build();
    }
}
