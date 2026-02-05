package com.gaming.catalog.repository;

import com.gaming.catalog.entity.Game;
import com.gaming.catalog.entity.Genre;
import com.gaming.catalog.entity.Platform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GameRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
        genreRepository.deleteAll();
        platformRepository.deleteAll();

        Genre action = genreRepository.save(Genre.builder().name("Action").build());
        Genre rpg = genreRepository.save(Genre.builder().name("RPG").build());

        Platform pc = platformRepository.save(Platform.builder().name("PC").build());
        Platform ps5 = platformRepository.save(Platform.builder().name("PS5").build());

        gameRepository.save(Game.builder()
                .title("The Witcher 3")
                .description("Open world RPG")
                .price(new BigDecimal("29.99"))
                .releaseDate(LocalDate.of(2015, 5, 19))
                .genres(Set.of(rpg, action))
                .platforms(Set.of(pc, ps5))
                .build());

        gameRepository.save(Game.builder()
                .title("Cyberpunk 2077")
                .description("Sci-fi RPG")
                .price(new BigDecimal("59.99"))
                .releaseDate(LocalDate.of(2020, 12, 10))
                .genres(Set.of(rpg))
                .platforms(Set.of(pc))
                .build());
    }

    @Test
    void shouldSearchGamesByTitle() {
        Page<Game> result = gameRepository.searchGames("Witcher", null, null, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("The Witcher 3");
    }

    @Test
    void shouldSearchGamesByGenre() {
        Page<Game> result = gameRepository.searchGames(null, "RPG", null, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void shouldSearchGamesByPlatform() {
        Page<Game> result = gameRepository.searchGames(null, null, "PS5", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("The Witcher 3");
    }
}
