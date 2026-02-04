-- User Profile Service - Database Schema
-- This script creates the users table with all necessary constraints and indexes

-- Drop table if exists (useful for development)
DROP TABLE IF EXISTS users CASCADE;

-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create indexes for performance
CREATE UNIQUE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_created_at ON users(created_at DESC);

-- Add comments for documentation
COMMENT ON TABLE users IS 'Stores user profile information synchronized from JWT authentication';
COMMENT ON COLUMN users.id IS 'Unique identifier for the user (UUID)';
COMMENT ON COLUMN users.email IS 'User email address from JWT claim (unique)';
COMMENT ON COLUMN users.username IS 'Username from JWT preferred_username claim';
COMMENT ON COLUMN users.avatar_url IS 'URL to user avatar image (optional)';
COMMENT ON COLUMN users.created_at IS 'Timestamp when the user profile was created';
COMMENT ON COLUMN users.updated_at IS 'Timestamp when the user profile was last updated';
