package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.request.CreateEventCodeRequest;
import com.charizad.compiled.gamification_service.domain.model.EventCode;
import com.charizad.compiled.gamification_service.domain.ports.out.EventCodeRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventCodeControllerTest {

    @Mock private EventCodeRepositoryPort eventCodeRepository;

    @InjectMocks private EventCodeController controller;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @DisplayName("POST /admin/event-codes returns 201 with created event code")
    void createEventCode_shouldReturn201() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        LocalDateTime validFrom = LocalDateTime.of(2026, 6, 1, 0, 0);
        LocalDateTime validUntil = LocalDateTime.of(2026, 6, 30, 23, 59);

        CreateEventCodeRequest request = new CreateEventCodeRequest("ABC123", validFrom, validUntil);

        EventCode saved = EventCode.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .code("ABC123")
                .validFrom(validFrom)
                .validUntil(validUntil)
                .usedByUserIds(new ArrayList<>())
                .build();

        when(eventCodeRepository.save(any())).thenReturn(saved);

        mockMvc.perform(post("/api/v1/gamificacion/admin/event-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.id").value("00000000-0000-0000-0000-000000000001"))
                .andExpect(jsonPath("$.timesUsed").value(0));
    }
}
