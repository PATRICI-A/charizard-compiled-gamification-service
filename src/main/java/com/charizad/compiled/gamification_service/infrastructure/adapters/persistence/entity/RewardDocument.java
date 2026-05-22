package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity;

import com.charizad.compiled.gamification_service.domain.model.enums.RewardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rewards")
public class RewardDocument {

    @Id
    private UUID id;

    private String name;
    private String description;
    private RewardType type;
    private int xpThreshold;
    private String iconUrl;
    private LocalDateTime createdAt;
    private boolean active;
}
