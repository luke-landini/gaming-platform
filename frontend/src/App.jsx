import { useState, useEffect } from 'react'
import UserProfile from './components/UserProfile'
import keycloak from './services/keycloak'
import './App.css'

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [isLoading, setIsLoading] = useState(true)

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
    <>
      {isAuthenticated ? (
        <UserProfile onLogout={handleLogout} />
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
    </>
  )
}

export default App
