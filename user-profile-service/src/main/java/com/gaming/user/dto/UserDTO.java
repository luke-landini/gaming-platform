package com.gaming.user.dto;

import com.gaming.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for User information.
 * Used to expose user data to clients without exposing the entity structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private UUID id;
    private String email;
    private String username;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Factory method to create a UserDTO from a UserEntity
     */
    public static UserDTO fromEntity(UserEntity entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .avatarUrl(entity.getAvatarUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
