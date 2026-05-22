package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity;

import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "badges")
public class BadgeDocument {

    @Id
    private UUID id;

    @Indexed(unique = true)
    private String name;

    private String description;
    private BadgeCategory category;
    private int xpReward;
    private String iconUrl;
    private LocalDateTime createdAt;
    private boolean active;
}
