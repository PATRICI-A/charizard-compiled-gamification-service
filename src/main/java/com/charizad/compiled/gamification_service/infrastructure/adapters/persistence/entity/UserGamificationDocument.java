package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_gamification")
public class UserGamificationDocument {

    @Id
    private UUID id;

    @Indexed(unique = true)
    private String userId;

    private int totalXp;
    private int weeklyXp;
    private int weeklyMonas;
    private int monthlyMonas;
    private int semestralMonas;
    private boolean rankingOptIn;

    private List<EarnedBadgeSubdocument> earnedBadges;
    private List<BadgeProgressSubdocument> progress;
    private List<EarnedRewardSubdocument> earnedRewards;
    private List<String> visitedCampusZones;
}
