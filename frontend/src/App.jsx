import { useState, useEffect } from 'react'
import UserProfile from './components/UserProfile'
import GameCatalog from './components/GameCatalog'
import Home from './components/Home'
import SnakeGame from './components/SnakeGame'
import keycloak from './services/keycloak'
import './App.css'

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const [activeTab, setActiveTab] = useState('home')

  useEffect(() => {
    // Initialize Keycloak
    keycloak.init({ 
      onLoad: 'check-sso',
      silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
      checkLoginIframe: false
    })
      .then((authenticated) => {
        setIsAuthenticated(authenticated)
        setIsLoading(false)
      })
      .catch((error) => {
        console.error('Keycloak initialization error:', error)
        setIsLoading(false)
      })
  }, [])

  const handleLogin = () => {
    keycloak.login()
  }

  const handleLogout = () => {
    keycloak.logout()
  }

  if (isLoading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading Gaming Platform...</p>
      </div>
    )
  }

  return (
    <div className="app-container">
      {isAuthenticated ? (
        <>
          <nav className="main-nav">
            <div className="nav-logo" onClick={() => setActiveTab('home')}>
              🎮 Gaming Platform
            </div>
            <div className="nav-links">
              <button 
                className={`nav-btn ${activeTab === 'catalog' ? 'active' : ''}`}
                onClick={() => setActiveTab('catalog')}
              >
                Game Catalog
              </button>
              <button 
                className={`nav-btn ${activeTab === 'profile' ? 'active' : ''}`}
                onClick={() => setActiveTab('profile')}
              >
                Profile
              </button>
              <button onClick={handleLogout} className="btn-logout-small">Logout</button>
            </div>
          </nav>

          <main className="content">
            {activeTab === 'home' && <Home onNavigate={setActiveTab} />}
            {activeTab === 'profile' && <UserProfile onLogout={handleLogout} />}
            {activeTab === 'catalog' && <GameCatalog onPlayGame={(gameId) => setActiveTab(gameId === 'snake-game' ? 'play' : 'catalog')} />}
            {activeTab === 'play' && <SnakeGame onBack={() => setActiveTab('catalog')} />}
          </main>
        </>
      ) : (
        <div className="login-welcome">
          <div className="welcome-content">
            <h1 className="welcome-title">🎮 Gaming Platform</h1>
            <p className="welcome-subtitle">Welcome to the ultimate gaming experience</p>
            <button 
              onClick={handleLogin}
              className="btn-login"
            >
              Login with Keycloak
            </button>
          </div>
        </div>
      )}
    </div>
  )
}

export default App
