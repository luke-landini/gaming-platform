package com.gaming.user.integration;

import com.gaming.user.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateNewUserOnFirstRequest() {
        // Given: A JWT token with user claims
        String email = "test@example.com";
        String username = "testuser";

        // When: Making a request to /api/v1/users/me with JWT
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(createMockJwtToken(email, username));

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<UserDTO> response = restTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.GET,
                request,
                UserDTO.class
        );

        // Then: User should be created and returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo(email);
        assertThat(response.getBody().getUsername()).isEqualTo(username);
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getCreatedAt()).isNotNull();
        assertThat(response.getBody().getAvatarUrl()).isNull();
    }

    @Test
    void shouldReturnExistingUserOnSecondRequest() {
        // Given: A user that was already created
        String email = "existing@example.com";
        String username = "existinguser";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(createMockJwtToken(email, username));
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // First request creates the user
        ResponseEntity<UserDTO> firstResponse = restTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.GET,
                request,
                UserDTO.class
        );

        // When: Making a second request with the same email
        ResponseEntity<UserDTO> secondResponse = restTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.GET,
                request,
                UserDTO.class
        );

        // Then: Should return the same user (not create a new one)
        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(secondResponse.getBody()).isNotNull();
        assertThat(secondResponse.getBody().getId()).isEqualTo(firstResponse.getBody().getId());
        assertThat(secondResponse.getBody().getEmail()).isEqualTo(email);
    }

    @Test
    void shouldReturn401WhenNoJwtProvided() {
        // When: Making a request without JWT token
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.GET,
                null,
                String.class
        );

        // Then: Should return 401 Unauthorized
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Creates a mock JWT token for testing purposes.
     * In a real test, you would use a proper JWT encoder or mock the security context.
     */
    private String createMockJwtToken(String email, String username) {
        // This is a simplified mock token
        // In production tests, use Spring Security's JWT testing utilities
        return "mock-jwt-token-" + email;
    }
}
