package com.gaming.user.service;

import com.gaming.user.dto.UserDTO;
import com.gaming.user.entity.UserEntity;
import com.gaming.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 * Uses Mockito to mock the repository layer.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserService userService;

    @Test
    void getOrCreate_ShouldReturnExistingUser_WhenUserExists() {
        // Given: An existing user in the database
        String email = "existing@example.com";
        String username = "existinguser";

        UserEntity existingUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .email(email)
                .username(username)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        // When: Calling getOrCreate
        UserDTO result = userService.getOrCreate(email, username);

        // Then: Should return the existing user without creating a new one
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getId()).isEqualTo(existingUser.getId());

        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).saveAndFlush(any(UserEntity.class));
    }

    @Test
    void getOrCreate_ShouldCreateNewUser_WhenUserDoesNotExist() {
        // Given: No existing user in the database
        String email = "newuser@example.com";
        String username = "newuser";

        UUID newUserId = UUID.randomUUID();
        UserEntity newUser = UserEntity.builder()
                .id(newUserId)
                .email(email)
                .username(username)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.saveAndFlush(any(UserEntity.class))).thenReturn(newUser);
        doNothing().when(entityManager).refresh(any());

        // When: Calling getOrCreate
        UserDTO result = userService.getOrCreate(email, username);

        // Then: Should create and return the new user
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getId()).isEqualTo(newUserId);

        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).saveAndFlush(any(UserEntity.class));
        verify(entityManager, times(1)).refresh(any());
    }

    @Test
    void getUserByEmail_ShouldReturnUser_WhenUserExists() {
        // Given: An existing user
        String email = "test@example.com";
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .email(email)
                .username("testuser")
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When: Calling getUserByEmail
        UserDTO result = userService.getUserByEmail(email);

        // Then: Should return the user
        assertThat(result.getEmail()).isEqualTo(email);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void getUserByEmail_ShouldThrowException_WhenUserNotFound() {
        // Given: No user in the database
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then: Should throw RuntimeException
        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with email: " + email);

        verify(userRepository, times(1)).findByEmail(email);
    }
}
