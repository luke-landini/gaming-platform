package com.gaming.leaderboard.controller;

import com.gaming.leaderboard.dto.LeaderboardEntryDTO;
import com.gaming.leaderboard.dto.ScoreResponseDTO;
import com.gaming.leaderboard.dto.ScoreSubmissionDTO;
import com.gaming.leaderboard.service.LeaderboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @PostMapping("/scores")
    @ResponseStatus(HttpStatus.CREATED)
    public ScoreResponseDTO submitScore(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ScoreSubmissionDTO submission) {
        String userId = jwt.getClaimAsString("email");
        String username = jwt.getClaimAsString("preferred_username");
        if (username == null) username = userId.split("@")[0];
        return leaderboardService.submitScore(userId, username, submission);
    }

    @GetMapping("/top/{gameId}")
    public List<LeaderboardEntryDTO> getTopScores(
            @PathVariable String gameId,
            @RequestParam(defaultValue = "10") int limit) {
        return leaderboardService.getTopScores(gameId, limit);
    }

    @GetMapping("/history/{gameId}")
    public List<ScoreResponseDTO> getUserHistory(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String gameId) {
        String userId = jwt.getClaimAsString("email");
        return leaderboardService.getUserHistory(userId, gameId);
    }

    @GetMapping("/history")
    public List<ScoreResponseDTO> getAllUserHistory(
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaimAsString("email");
        return leaderboardService.getAllUserHistory(userId);
    }
}
