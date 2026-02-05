package com.gaming.leaderboard.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "score_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String gameId;

    @Column(nullable = false)
    private Double score;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
