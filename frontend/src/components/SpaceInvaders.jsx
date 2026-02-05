import { useState, useEffect, useCallback, useRef } from 'react';
import { leaderboardApi } from '../services/api';
import './SpaceInvaders.css';

const CANVAS_WIDTH = 600;
const CANVAS_HEIGHT = 500;
const PLAYER_WIDTH = 40;
const PLAYER_HEIGHT = 20;
const INVADER_WIDTH = 30;
const INVADER_HEIGHT = 20;
const BULLET_WIDTH = 3;
const BULLET_HEIGHT = 10;

function SpaceInvaders({ onBack }) {
  const canvasRef = useRef(null);
  const [score, setScore] = useState(0);
  const [personalBest, setPersonalBest] = useState(0);
  const [gameOver, setGameOver] = useState(false);
  const [gameStarted, setGameStarted] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [highScores, setHighScores] = useState([]);

  // Game state refs to avoid closure issues in loop
  const gameState = useRef({
    player: { x: CANVAS_WIDTH / 2 - PLAYER_WIDTH / 2, y: CANVAS_HEIGHT - 30 },
    invaders: [],
    bullets: [],
    invaderBullets: [],
    direction: 1,
    moveDown: false,
    lastInvaderShot: 0
  });

  const keys = useRef({});

  const fetchLeaderboard = useCallback(async () => {
    try {
      const data = await leaderboardApi.getTopScores('space-invaders', 5);
      setHighScores(data);
      const history = await leaderboardApi.getUserHistory('space-invaders');
      if (history && history.length > 0) {
        setPersonalBest(Math.max(...history.map(h => h.score)));
      }
    } catch (err) {
      console.error('Failed to fetch leaderboard:', err);
    }
  }, []);

  useEffect(() => {
    fetchLeaderboard();
  }, [fetchLeaderboard]);

  const initInvaders = () => {
    const invaders = [];
    for (let row = 0; row < 4; row++) {
      for (let col = 0; col < 8; col++) {
        invaders.push({
          x: col * (INVADER_WIDTH + 20) + 50,
          y: row * (INVADER_HEIGHT + 20) + 50,
          alive: true
        });
      }
    }
    return invaders;
  };

  const startGame = () => {
    gameState.current = {
      player: { x: CANVAS_WIDTH / 2 - PLAYER_WIDTH / 2, y: CANVAS_HEIGHT - 30 },
      invaders: initInvaders(),
      bullets: [],
      invaderBullets: [],
      direction: 1,
      moveDown: false,
      lastInvaderShot: 0
    };
    setScore(0);
    setGameOver(false);
    setGameStarted(true);
  };

  const submitScore = async () => {
    setIsSubmitting(true);
    try {
      await leaderboardApi.submitScore({
        gameId: 'space-invaders',
        score: score,
      });
      await fetchLeaderboard();
      alert('Score submitted successfully!');
    } catch (err) {
      console.error('Failed to submit score:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  useEffect(() => {
    const handleKeyDown = (e) => {
      keys.current[e.key] = true;
      // Prevent default behavior for game keys to avoid scrolling/tabbing
      if ([' ', 'ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'Tab'].includes(e.key)) {
        e.preventDefault();
      }
    };
    const handleKeyUp = (e) => keys.current[e.key] = false;
    window.addEventListener('keydown', handleKeyDown);
    window.addEventListener('keyup', handleKeyUp);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      window.removeEventListener('keyup', handleKeyUp);
    };
  }, []);

  useEffect(() => {
    if (!gameStarted || gameOver) return;

    let animationFrameId;
    const ctx = canvasRef.current.getContext('2d');

    const update = () => {
      const state = gameState.current;

      // Player move
      if (keys.current['ArrowLeft'] && state.player.x > 0) state.player.x -= 5;
      if (keys.current['ArrowRight'] && state.player.x < CANVAS_WIDTH - PLAYER_WIDTH) state.player.x += 5;
      
      // Shoot
      if ((keys.current[' '] || keys.current['Tab']) && state.bullets.length < 3) {
        const now = Date.now();
        if (!state.lastShot || now - state.lastShot > 500) {
          state.bullets.push({ x: state.player.x + PLAYER_WIDTH / 2, y: state.player.y });
          state.lastShot = now;
        }
      }

      // Bullets update
      state.bullets = state.bullets.filter(b => b.y > 0);
      state.bullets.forEach(b => b.y -= 7);

      // Invaders move
      let hitEdge = false;
      state.invaders.forEach(inv => {
        if (!inv.alive) return;
        inv.x += state.direction * 2;
        if (inv.x <= 0 || inv.x >= CANVAS_WIDTH - INVADER_WIDTH) hitEdge = true;
      });

      if (hitEdge) {
        state.direction *= -1;
        state.invaders.forEach(inv => inv.y += 10);
      }

      // Collision bullet-invader
      state.bullets.forEach(b => {
        state.invaders.forEach(inv => {
          if (inv.alive && b.x > inv.x && b.x < inv.x + INVADER_WIDTH && b.y > inv.y && b.y < inv.y + INVADER_HEIGHT) {
            inv.alive = false;
            b.y = -10; // marked for removal
            setScore(s => s + 20);
          }
        });
      });

      // Invader shooting
      const now = Date.now();
      if (now - state.lastInvaderShot > 1500) {
        const aliveInvaders = state.invaders.filter(i => i.alive);
        if (aliveInvaders.length > 0) {
          const shooter = aliveInvaders[Math.floor(Math.random() * aliveInvaders.length)];
          state.invaderBullets.push({ x: shooter.x + INVADER_WIDTH / 2, y: shooter.y + INVADER_HEIGHT });
          state.lastInvaderShot = now;
        }
      }

      state.invaderBullets.forEach(b => b.y += 4);
      state.invaderBullets = state.invaderBullets.filter(b => b.y < CANVAS_HEIGHT);

      // Collision invaderBullet-player
      state.invaderBullets.forEach(b => {
        if (b.x > state.player.x && b.x < state.player.x + PLAYER_WIDTH && b.y > state.player.y && b.y < state.player.y + PLAYER_HEIGHT) {
          setGameOver(true);
          setGameStarted(false);
        }
      });

      // Check win
      if (state.invaders.every(i => !i.alive)) {
        setGameOver(true);
        setGameStarted(false);
      }
      
      // Check invader reach player
      if (state.invaders.some(i => i.alive && i.y + INVADER_HEIGHT >= state.player.y)) {
        setGameOver(true);
        setGameStarted(false);
      }
    };

    const draw = () => {
      const state = gameState.current;
      ctx.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

      // Draw player
      ctx.fillStyle = '#ff6b35';
      ctx.fillRect(state.player.x, state.player.y, PLAYER_WIDTH, PLAYER_HEIGHT);

      // Draw bullets
      ctx.fillStyle = '#fff';
      state.bullets.forEach(b => ctx.fillRect(b.x, b.y, BULLET_WIDTH, BULLET_HEIGHT));

      // Draw invader bullets
      ctx.fillStyle = '#ff4444';
      state.invaderBullets.forEach(b => ctx.fillRect(b.x, b.y, BULLET_WIDTH, BULLET_HEIGHT));

      // Draw invaders
      ctx.fillStyle = '#4caf50';
      state.invaders.forEach(inv => {
        if (inv.alive) ctx.fillRect(inv.x, inv.y, INVADER_WIDTH, INVADER_HEIGHT);
      });
    };

    const loop = () => {
      update();
      draw();
      if (gameStarted && !gameOver) {
        animationFrameId = requestAnimationFrame(loop);
      }
    };

    loop();
    return () => cancelAnimationFrame(animationFrameId);
  }, [gameStarted, gameOver]);

  return (
    <div className="space-invaders-container">
      <div className="game-area">
        <div className="game-header">
          <button className="btn-back" onClick={onBack}>← Back</button>
          <h2>🚀 Space Invaders</h2>
          <div className="score-container">
            <div className="score-display">Score: {score}</div>
            <div className="best-display">Best: {personalBest}</div>
          </div>
        </div>

        <div className="canvas-wrapper">
          <canvas 
            ref={canvasRef} 
            width={CANVAS_WIDTH} 
            height={CANVAS_HEIGHT}
            className="game-canvas"
          />
          
          {!gameStarted && !gameOver && (
            <div className="overlay">
              <button onClick={startGame} className="btn-start">Start Mission</button>
            </div>
          )}

          {gameOver && (
            <div className="overlay">
              <h3>{gameState.current.invaders.every(i => !i.alive) ? 'Mission Accomplished!' : 'Base Destroyed!'}</h3>
              <p>Final Score: {score}</p>
              <div className="overlay-buttons">
                <button onClick={startGame} className="btn-start">Restart</button>
                <button onClick={submitScore} className="btn-submit" disabled={isSubmitting || score === 0}>
                  {isSubmitting ? 'Transmitting...' : 'Submit Data'}
                </button>
              </div>
            </div>
          )}
        </div>
        <p className="controls-hint">Use Arrows to Move | Space or Tab to Shoot</p>
      </div>

      <div className="leaderboard-side">
        <h3>🏆 Top Commanders</h3>
        <ul className="high-scores">
          {highScores.map((entry, index) => (
            <li key={index} className="score-entry">
              <span className="rank">#{index + 1}</span>
              <span className="user">{entry.username || 'Unknown'}</span>
              <span className="points">{entry.score}</span>
            </li>
          ))}
          {highScores.length === 0 && <p className="no-scores">No records found</p>}
        </ul>
      </div>
    </div>
  );
}

export default SpaceInvaders;
