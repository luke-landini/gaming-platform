-- Initial data for Game Catalog
-- Insert some genres
INSERT INTO genres (name) VALUES ('Arcade'), ('Action'), ('Puzzle'), ('Sports') ON CONFLICT DO NOTHING;

-- Insert some platforms
INSERT INTO platforms (name) VALUES ('PC'), ('Web'), ('Mobile') ON CONFLICT DO NOTHING;

-- Insert Snake Game
INSERT INTO games (title, description, price, publisher, rating, release_date, created_at)
VALUES ('Snake Game', 'The classic retro snake game. Eat the food and grow longer!', 0.00, 'Retro Games Inc.', 4.5, CURRENT_DATE, CURRENT_TIMESTAMP);

-- Link Snake Game to Genres and Platforms (assuming IDs start at 1)
-- In a real scenario we'd use subqueries or let Hibernate handle this, but for init-db:
INSERT INTO game_genres (game_id, genre_id) 
SELECT g.id, gen.id FROM games g, genres gen WHERE g.title = 'Snake Game' AND gen.name = 'Arcade';

INSERT INTO game_platforms (game_id, platform_id)
SELECT g.id, p.id FROM games g, platforms p WHERE g.title = 'Snake Game' AND p.name = 'Web';
