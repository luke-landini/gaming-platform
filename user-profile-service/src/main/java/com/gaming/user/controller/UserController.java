package com.gaming.user.controller;

import com.gaming.user.dto.UserDTO;
import com.gaming.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for user profile operations.
 * All endpoints require JWT authentication.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final com.gaming.user.repository.UserRepository userRepository;

    /**
     * Get user profile by ID.
     *
     * @param id the user UUID
     * @return ResponseEntity with UserDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@org.springframework.web.bind.annotation.PathVariable java.util.UUID id) {
        log.info("GET /api/v1/users/{} - Request profile", id);
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(UserDTO.fromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get user profile by email.
     *
     * @param email the user email
     * @return ResponseEntity with UserDTO
     */
    @GetMapping("/email")
    public ResponseEntity<UserDTO> getUserByEmail(@org.springframework.web.bind.annotation.RequestParam String email) {
        log.info("GET /api/v1/users/email?email={} - Request profile", email);
        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(UserDTO.fromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get the current authenticated user's profile.
     * If the user doesn't exist in the database, it will be created automatically.
     *
     * @param jwt the JWT token containing user claims
     * @return ResponseEntity with UserDTO
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        log.info("GET /api/v1/users/me - Request from user: {}", jwt.getClaimAsString("email"));

        // Extract claims from JWT
        String email = jwt.getClaimAsString("email");
        String username = jwt.getClaimAsString("preferred_username");

        // Validate required claims
        if (email == null || email.isBlank()) {
            log.error("Missing 'email' claim in JWT");
            return ResponseEntity.badRequest().build();
        }

        if (username == null || username.isBlank()) {
            log.warn("Missing 'preferred_username' claim in JWT, using email as fallback");
            username = email.split("@")[0]; // Use email prefix as fallback
        }

        // Get or create user
        UserDTO userDTO = userService.getOrCreate(email, username);

        log.info("Successfully retrieved/created user: {} ({})", userDTO.getEmail(), userDTO.getId());
        return ResponseEntity.ok(userDTO);
    }
}
