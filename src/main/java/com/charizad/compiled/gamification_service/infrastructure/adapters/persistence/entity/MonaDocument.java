package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity;

import com.charizad.compiled.gamification_service.domain.model.enums.MonaCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Monas")
public class MonaDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String description;
    private MonaCategory category;
    private int xpReward;
    private String iconUrl;
    private LocalDateTime createdAt;
    private boolean active;
}
