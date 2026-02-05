package com.gaming.leaderboard.repository;

import com.gaming.leaderboard.model.ScoreRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScoreRecordRepository extends JpaRepository<ScoreRecord, UUID> {
    List<ScoreRecord> findByUserIdAndGameIdOrderByCreatedAtDesc(String userId, String gameId);
    List<ScoreRecord> findByUserIdOrderByCreatedAtDesc(String userId);
}
