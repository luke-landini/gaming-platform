import { useState, useEffect, useCallback, useRef } from 'react';
import { leaderboardApi } from '../services/api';
import './SnakeGame.css';

const GRID_SIZE = 20;
const INITIAL_SNAKE = [
  { x: 10, y: 10 },
  { x: 10, y: 11 },
  { x: 10, y: 12 },
];
const INITIAL_DIRECTION = { x: 0, y: -1 };
const GAME_SPEED = 150;

function SnakeGame({ onBack }) {
  const [snake, setSnake] = useState(INITIAL_SNAKE);
  const [food, setFood] = useState({ x: 5, y: 5 });
  const [direction, setDirection] = useState(INITIAL_DIRECTION);
  const [gameOver, setGameOver] = useState(false);
  const [score, setScore] = useState(0);
  const [gameStarted, setGameStarted] = useState(false);
  const [highScores, setHighScores] = useState([]);
  const [personalBest, setPersonalBest] = useState(0);
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const gameLoopRef = useRef();

  const generateFood = useCallback(() => {
    const newFood = {
      x: Math.floor(Math.random() * GRID_SIZE),
      y: Math.floor(Math.random() * GRID_SIZE),
    };
    return newFood;
  }, []);

  const fetchLeaderboard = useCallback(async () => {
    try {
      console.log('Fetching leaderboard for snake-game...');
      const data = await leaderboardApi.getTopScores('snake-game', 5);
      console.log('Leaderboard data received:', data);
      setHighScores(data);
      
      const history = await leaderboardApi.getUserHistory('snake-game');
      if (history && history.length > 0) {
        const best = Math.max(...history.map(h => h.score));
        setPersonalBest(best);
      }
    } catch (err) {
      console.error('Failed to fetch leaderboard:', err);
    }
  }, []);

  useEffect(() => {
    fetchLeaderboard();
  }, [fetchLeaderboard]);

  const resetGame = () => {
    setSnake(INITIAL_SNAKE);
    setFood(generateFood());
    setDirection(INITIAL_DIRECTION);
    setGameOver(false);
    setScore(0);
    setGameStarted(true);
  };

  const submitScore = async () => {
    setIsSubmitting(true);
    try {
      console.log('Submitting score:', score);
      await leaderboardApi.submitScore({
        gameId: 'snake-game',
        score: score,
      });
      console.log('Score submitted, refreshing leaderboard...');
      await fetchLeaderboard();
      alert('Score submitted successfully!');
    } catch (err) {
      console.error('Failed to submit score:', err);
      alert('Failed to submit score.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const moveSnake = useCallback(() => {
    setSnake((prevSnake) => {
      const head = prevSnake[0];
      const newHead = {
        x: head.x + direction.x,
        y: head.y + direction.y,
      };

      // Check collisions with walls
      if (
        newHead.x < 0 ||
        newHead.x >= GRID_SIZE ||
        newHead.y < 0 ||
        newHead.y >= GRID_SIZE
      ) {
        setGameOver(true);
        setGameStarted(false);
        return prevSnake;
      }

      // Check collision with self
      if (prevSnake.some((segment) => segment.x === newHead.x && segment.y === newHead.y)) {
        setGameOver(true);
        setGameStarted(false);
        return prevSnake;
      }

      const newSnake = [newHead, ...prevSnake];

      // Check if food is eaten
      if (newHead.x === food.x && newHead.y === food.y) {
        setScore((s) => s + 10);
        setFood(generateFood());
      } else {
        newSnake.pop();
      }

      return newSnake;
    });
  }, [direction, food, generateFood]);

  useEffect(() => {
    if (gameStarted && !gameOver) {
      gameLoopRef.current = setInterval(moveSnake, GAME_SPEED);
    } else {
      clearInterval(gameLoopRef.current);
    }
    return () => clearInterval(gameLoopRef.current);
  }, [gameStarted, gameOver, moveSnake]);

  useEffect(() => {
    const handleKeyDown = (e) => {
      switch (e.key) {
        case 'ArrowUp':
          if (direction.y === 0) setDirection({ x: 0, y: -1 });
          break;
        case 'ArrowDown':
          if (direction.y === 0) setDirection({ x: 0, y: 1 });
          break;
        case 'ArrowLeft':
          if (direction.x === 0) setDirection({ x: -1, y: 0 });
          break;
        case 'ArrowRight':
          if (direction.x === 0) setDirection({ x: 1, y: 0 });
          break;
        default:
          break;
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [direction]);

  return (
    <div className="snake-container">
      <div className="game-area">
        <div className="game-header">
          <button className="btn-back" onClick={onBack}>← Back</button>
          <h2>🐍 Snake Game</h2>
          <div className="score-container">
            <div className="score-display">Score: {score}</div>
            <div className="best-display">Best: {personalBest}</div>
          </div>
        </div>

        {!gameStarted && !gameOver && (
          <div className="overlay">
            <button onClick={resetGame} className="btn-start">Start Game</button>
          </div>
        )}

        {gameOver && (
          <div className="overlay">
            <h3>Game Over!</h3>
            <p>Final Score: {score}</p>
            <div className="overlay-buttons">
              <button onClick={resetGame} className="btn-start">Try Again</button>
              <button 
                onClick={submitScore} 
                className="btn-submit" 
                disabled={isSubmitting || score === 0}
              >
                {isSubmitting ? 'Submitting...' : 'Submit Score'}
              </button>
            </div>
          </div>
        )}

        <div className="grid">
          {Array.from({ length: GRID_SIZE * GRID_SIZE }).map((_, i) => {
            const x = i % GRID_SIZE;
            const y = Math.floor(i / GRID_SIZE);
            const isSnake = snake.some((s) => s.x === x && s.y === y);
            const isHead = snake[0].x === x && snake[0].y === y;
            const isFood = food.x === x && food.y === y;

            return (
              <div
                key={i}
                className={`cell ${isSnake ? 'snake' : ''} ${isHead ? 'head' : ''} ${isFood ? 'food' : ''}`}
              ></div>
            );
          })}
        </div>
      </div>

      <div className="leaderboard-side">
        <h3>🏆 Top Rank</h3>
        <ul className="high-scores">
          {highScores.map((entry, index) => (
            <li key={index} className="score-entry">
              <span className="rank">#{index + 1}</span>
              <span className="user">{entry.username || (entry.userId ? entry.userId.substring(0, 8) + '...' : 'Unknown')}</span>
              <span className="points">{entry.score}</span>
            </li>
          ))}
          {highScores.length === 0 && <p className="no-scores">No scores yet</p>}
        </ul>
      </div>
    </div>
  );
}

export default SnakeGame;
