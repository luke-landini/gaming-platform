package com.gaming.user.integration;

import com.gaming.user.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for UserController using Testcontainers.
 * Tests the complete flow: JWT authentication → controller → service → repository → PostgreSQL
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("gaming_users_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.sql.init.mode", () -> "always");
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateNewUserOnFirstRequest() throws Exception {
        // Given: A JWT token with user claims
        String email = "test@example.com";
        String username = "testuser";

        // When: Making a request to /api/v1/users/me with JWT
        mockMvc.perform(get("/api/v1/users/me")
                        .with(jwt()
                                .jwt(jwt -> jwt
                                        .claim("email", email)
                                        .claim("preferred_username", username)
                                )
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldReturnExistingUserOnSecondRequest() throws Exception {
        // Given: A user that was already created
        String email = "existing@example.com";
        String username = "existinguser";

        // First request creates the user
        mockMvc.perform(get("/api/v1/users/me")
                        .with(jwt()
                                .jwt(jwt -> jwt
                                        .claim("email", email)
                                        .claim("preferred_username", username)
                                )
                        ))
                .andExpect(status().isOk());

        // When: Making a second request with the same email
        mockMvc.perform(get("/api/v1/users/me")
                        .with(jwt()
                                .jwt(jwt -> jwt
                                        .claim("email", email)
                                        .claim("preferred_username", username)
                                )
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void shouldReturn401WhenNoJwtProvided() throws Exception {
        // When: Making a request without JWT token
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
