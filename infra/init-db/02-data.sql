-- Initial data for Game Catalog
-- Insert some genres
INSERT INTO genres (name) VALUES ('Arcade'), ('Action'), ('Puzzle'), ('Sports') ON CONFLICT DO NOTHING;

-- Insert some platforms
INSERT INTO platforms (name) VALUES ('PC'), ('Web'), ('Mobile') ON CONFLICT DO NOTHING;

-- Insert Snake Game
INSERT INTO games (title, description, price, publisher, rating, release_date, created_at, image_url)
VALUES ('Snake Game', 'The classic retro snake game. Eat the food and grow longer!', 0.00, 'Retro Games Inc.', 4.5, CURRENT_DATE, CURRENT_TIMESTAMP, 'https://images.unsplash.com/photo-1614732414444-096e5f1122d5?q=80&w=1000&auto=format&fit=crop');

-- Link Snake Game to Genres and Platforms (assuming IDs start at 1)
-- In a real scenario we'd use subqueries or let Hibernate handle this, but for init-db:
INSERT INTO game_genres (game_id, genre_id) 
SELECT g.id, gen.id FROM games g, genres gen WHERE g.title = 'Snake Game' AND gen.name = 'Arcade';

INSERT INTO game_platforms (game_id, platform_id)
SELECT g.id, p.id FROM games g, platforms p WHERE g.title = 'Snake Game' AND p.name = 'Web';

-- Insert Space Invaders
INSERT INTO games (title, description, price, publisher, rating, release_date, created_at, image_url)
VALUES ('Space Invaders', 'Protect the Earth from waves of alien invaders in this classic arcade shooter.', 4.99, 'Taito', 4.8, '1978-06-01', CURRENT_TIMESTAMP, 'https://images.unsplash.com/photo-1550745165-9bc0b252726f?q=80&w=1000&auto=format&fit=crop');

INSERT INTO game_genres (game_id, genre_id)
SELECT g.id, gen.id FROM games g, genres gen WHERE g.title = 'Space Invaders' AND gen.name = 'Action';

INSERT INTO game_platforms (game_id, platform_id)
SELECT g.id, p.id FROM games g, platforms p WHERE g.title = 'Space Invaders' AND p.name = 'Web';
