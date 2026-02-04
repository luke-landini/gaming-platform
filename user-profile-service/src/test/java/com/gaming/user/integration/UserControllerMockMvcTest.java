package com.gaming.user.integration;

import com.gaming.user.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for UserController using Testcontainers and MockMvc.
 * Tests the complete JWT authentication flow with a real PostgreSQL database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class UserControllerMockMvcTest {

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
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateNewUserWithValidJwt() throws Exception {
        // Given: JWT claims with email and preferred_username
        String email = "newuser@example.com";
        String username = "newuser";

        // When & Then: Request with JWT should create user and return 200
        mockMvc.perform(get("/api/v1/users/me")
                        .with(jwt()
                                .jwt(jwt -> jwt
                                        .claim("email", email)
                                        .claim("preferred_username", username)
                                )
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.avatarUrl").isEmpty());
    }

    @Test
    void shouldReturnSameUserOnSubsequentRequests() throws Exception {
        // Given: A user created with specific email
        String email = "repeat@example.com";
        String username = "repeatuser";

        // First request
        String firstResponse = mockMvc.perform(get("/api/v1/users/me")
                        .with(jwt()
                                .jwt(jwt -> jwt
                                        .claim("email", email)
                                        .claim("preferred_username", username)
                                )
                        ))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Second request with same email
        mockMvc.perform(get("/api/v1/users/me")
                        .with(jwt()
                                .jwt(jwt -> jwt
                                        .claim("email", email)
                                        .claim("preferred_username", username)
                                )
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void shouldReturn401WithoutJwt() throws Exception {
        // When: Request without JWT token
        // Then: Should return 401 Unauthorized
        mockMvc.perform(get("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldHandleMissingEmailClaim() throws Exception {
        // When: JWT without email claim
        // Then: Should return 400 Bad Request
        mockMvc.perform(get("/api/v1/users/me")
                        .with(jwt()
                                .jwt(jwt -> jwt
                                        .claim("preferred_username", "testuser")
                                )
                        ))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUseFallbackUsernameWhenPreferredUsernameMissing() throws Exception {
        // When: JWT without preferred_username claim
        String email = "nousername@example.com";

        // Then: Should use email prefix as username
        mockMvc.perform(get("/api/v1/users/me")
                        .with(jwt()
                                .jwt(jwt -> jwt
                                        .claim("email", email)
                                )
                        ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.username").value("nousername"));
    }
}
