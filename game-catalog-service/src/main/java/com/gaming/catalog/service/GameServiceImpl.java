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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final PlatformRepository platformRepository;
    private final GameMapper gameMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<GameDTO> getAllGames(Pageable pageable) {
        return gameRepository.findAll(pageable).map(gameMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public GameDTO getGameById(Long id) {
        return gameRepository.findById(id)
                .map(gameMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GameDTO> searchGames(String title, String genre, String platform, Pageable pageable) {
        return gameRepository.searchGames(title, genre, platform, pageable).map(gameMapper::toDto);
    }

    @Override
    @Transactional
    public GameDTO createGame(GameRequestDTO requestDTO) {
        Game game = gameMapper.toEntity(requestDTO);
        setGenresAndPlatforms(game, requestDTO);
        Game savedGame = gameRepository.save(game);
        return gameMapper.toDto(savedGame);
    }

    @Override
    @Transactional
    public GameDTO updateGame(Long id, GameRequestDTO requestDTO) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + id));
        
        gameMapper.updateEntity(requestDTO, game);
        setGenresAndPlatforms(game, requestDTO);
        
        Game updatedGame = gameRepository.save(game);
        return gameMapper.toDto(updatedGame);
    }

    @Override
    @Transactional
    public void deleteGame(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new ResourceNotFoundException("Game not found with id: " + id);
        }
        gameRepository.deleteById(id);
    }

    private void setGenresAndPlatforms(Game game, GameRequestDTO requestDTO) {
        if (requestDTO.getGenreIds() != null) {
            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(requestDTO.getGenreIds()));
            game.setGenres(genres);
        }
        
        if (requestDTO.getPlatformIds() != null) {
            Set<Platform> platforms = new HashSet<>(platformRepository.findAllById(requestDTO.getPlatformIds()));
            game.setPlatforms(platforms);
        }
    }
}
