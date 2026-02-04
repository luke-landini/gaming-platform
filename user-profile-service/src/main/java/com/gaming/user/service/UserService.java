package com.gaming.user.service;

import com.gaming.user.dto.UserDTO;
import com.gaming.user.entity.UserEntity;
import com.gaming.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing user profiles.
 * Handles business logic for user operations including creation and retrieval.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Get an existing user by email or create a new one if not found.
     * This method is idempotent and thread-safe.
     *
     * @param email    the user's email from JWT claims
     * @param username the user's username from JWT claims
     * @return UserDTO containing the user's information
     */
    @Transactional
    public UserDTO getOrCreate(String email, String username) {
        log.debug("Looking up user with email: {}", email);

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("User not found with email: {}. Creating new user.", email);
                    return createNewUser(email, username);
                });

        log.debug("Returning user: {} ({})", user.getUsername(), user.getId());
        return UserDTO.fromEntity(user);
    }

    /**
     * Creates a new user in the database.
     *
     * @param email    the user's email
     * @param username the user's username
     * @return the newly created UserEntity
     */
    private UserEntity createNewUser(String email, String username) {
        UserEntity newUser = UserEntity.builder()
                .email(email)
                .username(username)
                .avatarUrl(null) // Initially null, can be updated later
                .build();

        UserEntity savedUser = userRepository.save(newUser);
        log.info("Created new user with ID: {} for email: {}", savedUser.getId(), email);

        return savedUser;
    }

    /**
     * Get user by email.
     *
     * @param email the user's email
     * @return UserDTO if found
     * @throws RuntimeException if user not found
     */
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);

        return userRepository.findByEmail(email)
                .map(UserDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}
