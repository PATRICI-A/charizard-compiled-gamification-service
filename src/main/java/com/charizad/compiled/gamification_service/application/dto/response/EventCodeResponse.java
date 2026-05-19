package com.charizad.compiled.gamification_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCodeResponse {
    private String id;
    private String code;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private int timesUsed;
}
