package com.gaming.leaderboard.service;

import com.gaming.leaderboard.dto.LeaderboardEntryDTO;
import com.gaming.leaderboard.dto.ScoreResponseDTO;
import com.gaming.leaderboard.dto.ScoreSubmissionDTO;

import java.util.List;

public interface LeaderboardService {
    ScoreResponseDTO submitScore(String userId, String username, ScoreSubmissionDTO submission);
    List<LeaderboardEntryDTO> getTopScores(String gameId, int limit);
    List<ScoreResponseDTO> getUserHistory(String userId, String gameId);
    List<ScoreResponseDTO> getAllUserHistory(String userId);
}
