import { useState, useEffect } from 'react';
import { catalogApi } from '../services/api';
import './Home.css';

function Home({ onNavigate }) {
  const [featuredGames, setFeaturedGames] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchFeatured = async () => {
      try {
        const data = await catalogApi.getGames({ size: 3 });
        setFeaturedGames(data.content || []);
      } catch (error) {
        console.error('Error fetching featured games:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchFeatured();
  }, []);

  return (
    <div className="home-container">
      <section className="hero-section">
        <div className="hero-content">
          <h1 className="hero-title">Welcome to the <span className="highlight">Gaming Platform</span></h1>
          <p className="hero-subtitle">
            Discover the latest games, manage your profile, and connect with the community.
            Your ultimate gaming destination starts here.
          </p>
          <div className="hero-actions">
            <button className="btn-primary" onClick={() => onNavigate('catalog')}>
              Explore Catalog 🎮
            </button>
          </div>
        </div>
        <div className="hero-overlay"></div>
      </section>

      <section className="features-section">
        <div className="section-header">
          <h2>Why Choose Us?</h2>
          <div className="header-line"></div>
        </div>
        <div className="features-grid">
          <div className="feature-card">
            <div className="feature-icon">🎮</div>
            <h3>Vast Catalog</h3>
            <p>Access a growing library of games across all genres and platforms.</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">🛡️</div>
            <h3>Secure Access</h3>
            <p>Integrated with Keycloak for industry-standard security and single sign-on.</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">⚡</div>
            <h3>Fast Performance</h3>
            <p>Built with React 19 and Vite for a lightning-fast user experience.</p>
          </div>
        </div>
      </section>

      <section className="featured-games-section">
        <div className="section-header">
          <h2>Featured Games</h2>
          <div className="header-line"></div>
        </div>
        
        {loading ? (
          <div className="home-loading">
            <div className="spinner-small"></div>
          </div>
        ) : (
          <div className="featured-grid">
            {featuredGames.length > 0 ? (
              featuredGames.map(game => (
                <div key={game.id} className="featured-game-card">
                  <div className="game-card-img">
                    {game.imageUrl ? (
                      <img src={game.imageUrl} alt={game.title} className="home-game-img" />
                    ) : (
                      <div className="home-game-placeholder">🕹️</div>
                    )}
                    <span className="game-rating">★ {game.rating}</span>
                  </div>
                  <div className="game-card-info">
                    <h3>{game.title}</h3>
                    <p>{game.publisher}</p>
                    <span className="game-price">€{game.price}</span>
                  </div>
                </div>
              ))
            ) : (
              <p className="no-data">Explore our catalog to find your next favorite game!</p>
            )}
          </div>
        )}
        
        <div className="view-all-container">
          <button className="btn-link" onClick={() => onNavigate('catalog')}>
            View All Games →
          </button>
        </div>
      </section>

    </div>
  );
}

export default Home;
