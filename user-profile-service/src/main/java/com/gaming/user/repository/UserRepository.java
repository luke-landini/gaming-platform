package com.gaming.user.repository;

import com.gaming.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity operations.
 * Extends JpaRepository to provide CRUD operations and custom queries.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Find a user by their email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Check if a user exists with the given email.
     *
     * @param email the email address to check
     * @return true if a user exists with the email, false otherwise
     */
    boolean existsByEmail(String email);
}
