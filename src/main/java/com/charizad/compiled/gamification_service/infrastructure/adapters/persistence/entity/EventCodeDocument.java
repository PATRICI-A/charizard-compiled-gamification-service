package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "event_codes")
public class EventCodeDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String code;

    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private List<String> usedByUserIds;
}
