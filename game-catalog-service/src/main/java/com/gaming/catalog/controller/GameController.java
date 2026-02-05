package com.gaming.catalog.controller;

import com.gaming.catalog.dto.GameDTO;
import com.gaming.catalog.dto.GameRequestDTO;
import com.gaming.catalog.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping
    public ResponseEntity<Page<GameDTO>> getAllGames(Pageable pageable) {
        return ResponseEntity.ok(gameService.getAllGames(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGameById(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGameById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<GameDTO>> searchGames(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String platform,
            Pageable pageable) {
        return ResponseEntity.ok(gameService.searchGames(title, genre, platform, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameDTO> createGame(@Valid @RequestBody GameRequestDTO requestDTO) {
        return new ResponseEntity<>(gameService.createGame(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameDTO> updateGame(@PathVariable Long id, @Valid @RequestBody GameRequestDTO requestDTO) {
        return ResponseEntity.ok(gameService.updateGame(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}
