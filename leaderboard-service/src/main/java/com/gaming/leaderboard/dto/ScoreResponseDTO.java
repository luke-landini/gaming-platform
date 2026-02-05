package com.gaming.leaderboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreResponseDTO {
    private UUID id;
    private String userId;
    private String gameId;
    private Double score;
    private LocalDateTime createdAt;
}
