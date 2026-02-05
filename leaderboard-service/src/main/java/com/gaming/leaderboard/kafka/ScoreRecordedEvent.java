package com.gaming.leaderboard.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreRecordedEvent {
    private String userId;
    private String gameId;
    private Double score;
    private LocalDateTime timestamp;
}
