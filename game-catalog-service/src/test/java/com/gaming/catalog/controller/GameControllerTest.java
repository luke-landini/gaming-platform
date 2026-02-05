package com.gaming.catalog.controller;

import com.gaming.catalog.dto.GameDTO;
import com.gaming.catalog.dto.GameRequestDTO;
import com.gaming.catalog.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.context.annotation.Import;
import com.gaming.catalog.config.SecurityConfig;

@WebMvcTest(GameController.class)
@Import(SecurityConfig.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAllGames_ShouldReturnOk() throws Exception {
        when(gameService.getAllGames(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getGameById_ShouldReturnGame() throws Exception {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setId(1L);
        gameDTO.setTitle("Test Game");
        
        when(gameService.getGameById(1L)).thenReturn(gameDTO);

        mockMvc.perform(get("/api/games/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Game"));
    }

    @Test
    void createGame_AsAdmin_ShouldReturnCreated() throws Exception {
        GameRequestDTO requestDTO = new GameRequestDTO();
        requestDTO.setTitle("New Game");
        requestDTO.setPrice(new BigDecimal("59.99"));

        GameDTO responseDTO = new GameDTO();
        responseDTO.setId(1L);
        responseDTO.setTitle("New Game");

        when(gameService.createGame(any(GameRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/games")
                        .with(jwt().authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Game"));
    }

    @Test
    void createGame_AsUser_ShouldReturnForbidden() throws Exception {
        GameRequestDTO requestDTO = new GameRequestDTO();
        requestDTO.setTitle("New Game");
        requestDTO.setPrice(new BigDecimal("59.99"));

        mockMvc.perform(post("/api/games")
                        .with(jwt().authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }
}
