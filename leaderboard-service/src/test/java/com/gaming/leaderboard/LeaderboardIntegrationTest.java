package com.gaming.leaderboard;

import com.gaming.leaderboard.dto.LeaderboardEntryDTO;
import com.gaming.leaderboard.dto.ScoreResponseDTO;
import com.gaming.leaderboard.dto.ScoreSubmissionDTO;
import com.gaming.leaderboard.model.ScoreRecord;
import com.gaming.leaderboard.repository.ScoreRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = {LeaderboardIntegrationTest.Initializer.class})
public class LeaderboardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScoreRecordRepository scoreRecordRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @Container
    public static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.data.redis.host=" + redisContainer.getHost(),
                    "spring.data.redis.port=" + redisContainer.getMappedPort(6379),
                    "spring.kafka.bootstrap-servers=" + kafkaContainer.getBootstrapServers()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @BeforeEach
    void setUp() {
        scoreRecordRepository.deleteAll();
    }

    @Test
    void submitScore_ShouldReturnCreated() throws Exception {
        ScoreSubmissionDTO submission = new ScoreSubmissionDTO("game-1", 100.0);

        mockMvc.perform(post("/api/leaderboard/scores")
                        .with(jwt().jwt(j -> j.subject("user-1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user-1"))
                .andExpect(jsonPath("$.gameId").value("game-1"))
                .andExpect(jsonPath("$.score").value(100.0));

        assertThat(scoreRecordRepository.findAll()).hasSize(1);
    }

    @Test
    void getTopScores_ShouldReturnList() throws Exception {
        // Submit some scores
        submitScore("user-1", "game-1", 100.0);
        submitScore("user-2", "game-1", 200.0);
        submitScore("user-3", "game-1", 150.0);

        mockMvc.perform(get("/api/leaderboard/top/game-1")
                        .with(jwt().jwt(j -> j.subject("user-1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].userId").value("user-2"))
                .andExpect(jsonPath("$[0].score").value(200.0))
                .andExpect(jsonPath("$[1].userId").value("user-3"))
                .andExpect(jsonPath("$[1].score").value(150.0))
                .andExpect(jsonPath("$[2].userId").value("user-1"))
                .andExpect(jsonPath("$[2].score").value(100.0));
    }

    @Test
    void getUserHistory_ShouldReturnHistory() throws Exception {
        submitScore("user-1", "game-1", 100.0);
        submitScore("user-1", "game-1", 120.0);

        mockMvc.perform(get("/api/leaderboard/history/game-1")
                        .with(jwt().jwt(j -> j.subject("user-1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].score").value(120.0))
                .andExpect(jsonPath("$[1].score").value(100.0));
    }

    private void submitScore(String userId, String gameId, Double score) throws Exception {
        ScoreSubmissionDTO submission = new ScoreSubmissionDTO(gameId, score);
        mockMvc.perform(post("/api/leaderboard/scores")
                        .with(jwt().jwt(j -> j.subject(userId)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isCreated());
    }
}
