package com.gaming.leaderboard.service;

import com.gaming.leaderboard.dto.LeaderboardEntryDTO;
import com.gaming.leaderboard.dto.ScoreResponseDTO;
import com.gaming.leaderboard.dto.ScoreSubmissionDTO;
import com.gaming.leaderboard.kafka.ScoreRecordedEvent;
import com.gaming.leaderboard.model.ScoreRecord;
import com.gaming.leaderboard.repository.ScoreRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final ScoreRecordRepository scoreRecordRepository;
    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, ScoreRecordedEvent> kafkaTemplate;
    private final RestTemplate restTemplate;

    @Value("${services.user-profile.url:http://localhost:8081}")
    private String userProfileUrl;

    private static final String LEADERBOARD_KEY_PREFIX = "leaderboard:";
    private static final String TOPIC_SCORE_RECORDED = "score-recorded";

    @Override
    @Transactional
    public ScoreResponseDTO submitScore(String userId, String username, ScoreSubmissionDTO submission) {
        log.debug("Submitting score for user {} ({}): {}", userId, username, submission);

        // 1. Save to PostgreSQL
        ScoreRecord record = ScoreRecord.builder()
                .userId(userId)
                .gameId(submission.getGameId())
                .score(submission.getScore())
                .build();
        ScoreRecord savedRecord = scoreRecordRepository.save(record);

        // 2. Update Redis Sorted Set (only if it's the new high score)
        String redisKey = LEADERBOARD_KEY_PREFIX + submission.getGameId();
        
        Double currentScore = redisTemplate.opsForZSet().score(redisKey, userId);
        log.debug("Current score in Redis for user {}: {}. New score: {}", userId, currentScore, submission.getScore());
        
        // Safety check: if Redis is missing or outdated compared to DB, we should also check DB best score
        if (currentScore == null) {
            Double dbBestScore = scoreRecordRepository.findByUserIdAndGameIdOrderByCreatedAtDesc(userId, submission.getGameId())
                    .stream()
                    .mapToDouble(ScoreRecord::getScore)
                    .max()
                    .orElse(0.0);
            currentScore = dbBestScore > 0 ? dbBestScore : null;
            if (currentScore != null) {
                log.info("Redis was empty but found best score in DB for user {}: {}. Syncing to Redis.", userId, currentScore);
                redisTemplate.opsForZSet().add(redisKey, userId, currentScore);
            }
        }

        if (currentScore == null || submission.getScore() > currentScore) {
            log.info("Updating Redis leaderboard for user {} with new high score: {}. Previous best: {}", userId, submission.getScore(), currentScore);
            redisTemplate.opsForZSet().add(redisKey, userId, submission.getScore());
        } else {
            log.debug("Score {} is not higher than current best {}, skipping Redis update", submission.getScore(), currentScore);
        }

        // 3. Publish Kafka Event
        ScoreRecordedEvent event = ScoreRecordedEvent.builder()
                .userId(userId)
                .gameId(submission.getGameId())
                .score(submission.getScore())
                .timestamp(LocalDateTime.now())
                .build();
        kafkaTemplate.send(TOPIC_SCORE_RECORDED, event);

        return mapToResponse(savedRecord);
    }

    @Override
    public List<LeaderboardEntryDTO> getTopScores(String gameId, int limit) {
        String redisKey = LEADERBOARD_KEY_PREFIX + gameId;
        Set<ZSetOperations.TypedTuple<String>> topScores = redisTemplate.opsForZSet()
                .reverseRangeWithScores(redisKey, 0, limit - 1);

        if (topScores == null) {
            return List.of();
        }

        return topScores.stream()
                .map(tuple -> {
                    String userId = tuple.getValue();
                    String username = fetchUsername(userId);
                    return LeaderboardEntryDTO.builder()
                            .userId(userId)
                            .username(username)
                            .score(tuple.getScore())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String fetchUsername(String userId) {
        try {
            // Se userId è un email, proviamo a recuperare via email
            String url;
            if (userId.contains("@")) {
                url = userProfileUrl + "/api/v1/users/email?email=" + userId;
            } else {
                url = userProfileUrl + "/api/v1/users/" + userId;
            }
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("username")) {
                return (String) response.get("username");
            }
        } catch (Exception e) {
            log.warn("Could not fetch username for userId {}: {}", userId, e.getMessage());
        }
        return "Unknown";
    }

    @Override
    public List<ScoreResponseDTO> getUserHistory(String userId, String gameId) {
        return scoreRecordRepository.findByUserIdAndGameIdOrderByCreatedAtDesc(userId, gameId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScoreResponseDTO> getAllUserHistory(String userId) {
        log.info("Fetching all user history for userId: {}", userId);
        List<ScoreRecord> records = scoreRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
        log.info("Found {} records for userId: {}", records.size(), userId);
        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ScoreResponseDTO mapToResponse(ScoreRecord record) {
        return ScoreResponseDTO.builder()
                .id(record.getId())
                .userId(record.getUserId())
                .gameId(record.getGameId())
                .score(record.getScore())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
