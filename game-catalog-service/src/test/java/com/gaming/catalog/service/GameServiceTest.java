package com.gaming.catalog.service;

import com.gaming.catalog.dto.GameDTO;
import com.gaming.catalog.dto.GameRequestDTO;
import com.gaming.catalog.entity.Game;
import com.gaming.catalog.entity.Genre;
import com.gaming.catalog.entity.Platform;
import com.gaming.catalog.exception.ResourceNotFoundException;
import com.gaming.catalog.mapper.GameMapper;
import com.gaming.catalog.repository.GameRepository;
import com.gaming.catalog.repository.GenreRepository;
import com.gaming.catalog.repository.PlatformRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private PlatformRepository platformRepository;
    @Mock
    private GameMapper gameMapper;

    @InjectMocks
    private GameServiceImpl gameService;

    private Game game;
    private GameDTO gameDTO;
    private GameRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        game = new Game();
        game.setId(1L);
        game.setTitle("Test Game");

        gameDTO = new GameDTO();
        gameDTO.setId(1L);
        gameDTO.setTitle("Test Game");

        requestDTO = new GameRequestDTO();
        requestDTO.setTitle("Test Game");
        requestDTO.setGenreIds(Set.of(1L));
        requestDTO.setPlatformIds(Set.of(1L));
    }

    @Test
    void getGameById_WhenExists_ShouldReturnGame() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(gameMapper.toDto(game)).thenReturn(gameDTO);

        GameDTO result = gameService.getGameById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(gameRepository).findById(1L);
    }

    @Test
    void getGameById_WhenNotExists_ShouldThrowException() {
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gameService.getGameById(1L));
    }

    @Test
    void createGame_ShouldReturnSavedGame() {
        when(gameMapper.toEntity(any(GameRequestDTO.class))).thenReturn(game);
        when(genreRepository.findAllById(any())).thenReturn(List.of(new Genre()));
        when(platformRepository.findAllById(any())).thenReturn(List.of(new Platform()));
        when(gameRepository.save(any(Game.class))).thenReturn(game);
        when(gameMapper.toDto(game)).thenReturn(gameDTO);

        GameDTO result = gameService.createGame(requestDTO);

        assertNotNull(result);
        assertEquals("Test Game", result.getTitle());
        verify(gameRepository).save(any(Game.class));
    }
}
