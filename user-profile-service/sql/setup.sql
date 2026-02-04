-- Setup iniziale database gaming_users
-- Esegui questo script con: psql -U postgres -f setup.sql

-- Crea il database
CREATE DATABASE gaming_users
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Italian_Italy.1252'
    LC_CTYPE = 'Italian_Italy.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Connettiti al database
\c gaming_users

-- Query utili per debug

-- 1. Visualizza tutti gli utenti
SELECT * FROM users ORDER BY created_at DESC;

-- 2. Conta utenti totali
SELECT COUNT(*) as total_users FROM users;

-- 3. Trova utente per email
SELECT * FROM users WHERE email = 'user@example.com';

-- 4. Utenti creati oggi
SELECT * FROM users
WHERE DATE(created_at) = CURRENT_DATE
ORDER BY created_at DESC;

-- 5. Utenti senza avatar
SELECT id, email, username, created_at
FROM users
WHERE avatar_url IS NULL
ORDER BY created_at DESC;

-- 6. Drop e ricrea tabella (ATTENZIONE: cancella tutti i dati!)
-- DROP TABLE IF EXISTS users CASCADE;

-- 7. Reset sequence (se necessario)
-- Non necessario con UUID, ma per altri ID:
-- ALTER SEQUENCE users_id_seq RESTART WITH 1;

-- 8. Backup utenti
-- COPY users TO 'C:/backup/users.csv' DELIMITER ',' CSV HEADER;

-- 9. Restore utenti
-- COPY users FROM 'C:/backup/users.csv' DELIMITER ',' CSV HEADER;

-- 10. Statistiche database
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
