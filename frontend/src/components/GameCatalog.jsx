import { useState, useEffect } from 'react';
import { catalogApi } from '../services/api';
import keycloak from '../services/keycloak';
import './GameCatalog.css';

function GameCatalog({ onPlayGame }) {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [isAdmin, setIsAdmin] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [newGame, setNewGame] = useState({
    title: '',
    description: '',
    price: 0,
    releaseDate: '',
    publisher: '',
    rating: 0,
    genreNames: [],
    platformNames: []
  });

  useEffect(() => {
    fetchGames();
    setIsAdmin(keycloak.hasResourceRole('admin', 'gaming-frontend') || keycloak.hasRealmRole('admin'));
  }, []);

  const fetchGames = async (search = '') => {
    try {
      setLoading(true);
      let data;
      if (search) {
        data = await catalogApi.searchGames({ title: search });
      } else {
        data = await catalogApi.getGames();
      }
      setGames(data.content || []);
    } catch (err) {
      console.error('Error fetching games:', err);
      setError('Failed to load games catalog');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    fetchGames(searchTerm);
  };

  const handleCreateGame = async (e) => {
    e.preventDefault();
    try {
      await catalogApi.createGame(newGame);
      setShowModal(false);
      fetchGames();
      setNewGame({
        title: '', description: '', price: 0, releaseDate: '',
        publisher: '', rating: 0, genreNames: [], platformNames: []
      });
    } catch (err) {
      console.error('Error creating game:', err);
      alert('Error creating game. Check permissions or data.');
    }
  };

  if (loading && !games.length) return <div className="loading">Loading Games...</div>;

  return (
    <div className="game-list-container">
      <div className="catalog-header">
        <h1>🎮 Game Catalog</h1>
        {isAdmin && (
          <button className="btn-add-game" onClick={() => setShowModal(true)}>
            + Add New Game
          </button>
        )}
      </div>

      <form className="search-bar" onSubmit={handleSearch}>
        <input 
          type="text" 
          placeholder="Search by title..." 
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <button type="submit" className="btn btn-primary">Search</button>
      </form>

      {error && <div className="error-message">{error}</div>}

      <div className="game-grid">
        {games.map(game => (
          <div key={game.id} className="game-card">
            <div className="game-image-placeholder">🕹️</div>
            <div className="game-card-content">
              <h3>{game.title}</h3>
              <p className="game-price">€{game.price.toFixed(2)}</p>
              <p>{game.description?.substring(0, 100)}...</p>
              <div className="game-meta">
                {game.genres?.map(g => (
                  <span key={g.id} className="badge genre-badge">{g.name}</span>
                ))}
                {game.platforms?.map(p => (
                  <span key={p.id} className="badge platform-badge">{p.name}</span>
                ))}
              </div>
              <div className="game-actions">
                <button 
                  className="btn-play" 
                  onClick={() => onPlayGame(game.title.toLowerCase().includes('snake') ? 'snake-game' : game.id)}
                >
                  ▶ Play
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="game-form-modal">
            <h2>Add New Game</h2>
            <form onSubmit={handleCreateGame}>
              <div className="form-group">
                <label>Title</label>
                <input required value={newGame.title} onChange={e => setNewGame({...newGame, title: e.target.value})} />
              </div>
              <div className="form-group">
                <label>Price</label>
                <input type="number" step="0.01" required value={newGame.price} onChange={e => setNewGame({...newGame, price: parseFloat(e.target.value)})} />
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea value={newGame.description} onChange={e => setNewGame({...newGame, description: e.target.value})} />
              </div>
              <div className="form-group">
                <label>Publisher</label>
                <input value={newGame.publisher} onChange={e => setNewGame({...newGame, publisher: e.target.value})} />
              </div>
              <div className="form-actions">
                <button type="button" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn-add-game">Create</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default GameCatalog;
