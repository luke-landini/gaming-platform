package com.gaming.catalog.service;

import com.gaming.catalog.dto.GameDTO;
import com.gaming.catalog.dto.GameRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GameService {
    Page<GameDTO> getAllGames(Pageable pageable);
    GameDTO getGameById(Long id);
    Page<GameDTO> searchGames(String title, String genre, String platform, Pageable pageable);
    GameDTO createGame(GameRequestDTO requestDTO);
    GameDTO updateGame(Long id, GameRequestDTO requestDTO);
    void deleteGame(Long id);
}
