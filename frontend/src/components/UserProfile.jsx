import { useState, useEffect } from 'react';
import { userApi, leaderboardApi } from '../services/api';
import './UserProfile.css';

function UserProfile({ onLogout }) {
  const [user, setUser] = useState(null);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [stats, setStats] = useState({
    gamesPlayed: 0,
    level: 1,
    achievements: 0
  });

  useEffect(() => {
    fetchUserProfile();
    fetchUserHistory();
  }, []);

  useEffect(() => {
    calculateStats(history);
  }, [history]);

  const fetchUserProfile = async () => {
    try {
      setLoading(true);
      setError(null);
      const userData = await userApi.getCurrentUser();
      setUser(userData);
    } catch (err) {
      console.error('Error fetching user profile:', err);
      setError(err.response?.data?.message || 'Failed to load user profile');

      // If unauthorized, trigger logout
      if (err.response?.status === 401) {
        setTimeout(() => onLogout(), 2000);
      }
    } finally {
      setLoading(false);
    }
  };

  const fetchUserHistory = async () => {
    try {
      const data = await leaderboardApi.getAllUserHistory();
      setHistory(data);
    } catch (err) {
      console.error('Error fetching user history:', err);
    }
  };

  const calculateStats = (historyData) => {
    if (!historyData || historyData.length === 0) {
      setStats({
        gamesPlayed: 0,
        level: 1,
        achievements: 0
      });
      return;
    }
    // 1. Games Played
    const gamesPlayed = historyData.length;

    // 2. Level calculation (e.g., 100 points per level)
    const totalScore = historyData.reduce((acc, curr) => acc + curr.score, 0);
    const level = Math.floor(totalScore / 100) + 1;

    // 3. Achievements logic
    let achievements = 0;
    if (gamesPlayed >= 1) achievements++; // First Game
    if (gamesPlayed >= 10) achievements++; // Veteran (10 games)
    if (historyData.some(h => h.score >= 50)) achievements++; // Sharpshooter (Score >= 50)
    if (historyData.some(h => h.score >= 100)) achievements++; // Master (Score >= 100)
    
    // Unicitá dei giochi provati
    const uniqueGames = new Set(historyData.map(h => h.gameId)).size;
    if (uniqueGames >= 3) achievements++; // Explorer (3 unique games)

    setStats({
      gamesPlayed,
      level,
      achievements
    });
  };

  const handleLogout = () => {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    onLogout();
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('it-IT', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getInitials = (username) => {
    if (!username) return '?';
    return username.substring(0, 2).toUpperCase();
  };

  if (loading) {
    return (
      <div className="profile-container">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Loading profile...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="profile-container">
        <div className="error-container">
          <div className="error-icon">⚠️</div>
          <h2>Error Loading Profile</h2>
          <p>{error}</p>
          <button onClick={fetchUserProfile} className="btn btn-primary">
            Retry
          </button>
          <button onClick={handleLogout} className="btn btn-secondary">
            Back to Login
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="profile-container">
      <div className="profile-card">
        <div className="profile-banner">
          <div className="banner-gradient"></div>
        </div>

        <div className="profile-content">
          <div className="avatar-section">
            {user.avatarUrl ? (
              <img src={user.avatarUrl} alt="Avatar" className="avatar" />
            ) : (
              <div className="avatar avatar-placeholder">
                {getInitials(user.username)}
              </div>
            )}
            <div className="status-badge">Active</div>
          </div>

          <div className="profile-info">
            <h2>{user.username}</h2>
            <p className="user-email">{user.email}</p>
            <p className="user-id">ID: {user.id}</p>
          </div>

          <div className="profile-details">
            <div className="detail-card">
              <div className="detail-icon">📅</div>
              <div className="detail-content">
                <h3>Member Since</h3>
                <p>{formatDate(user.createdAt)}</p>
              </div>
            </div>

            <div className="detail-card">
              <div className="detail-icon">🔄</div>
              <div className="detail-content">
                <h3>Last Updated</h3>
                <p>{formatDate(user.updatedAt)}</p>
              </div>
            </div>
          </div>

          <div className="profile-actions">
            <button onClick={() => { fetchUserProfile(); fetchUserHistory(); }} className="btn btn-primary">
              🔄 Refresh Profile
            </button>
          </div>
        </div>
      </div>

      {history.length > 0 && (
        <div className="history-section">
          <h2>📊 Recent Game Activity</h2>
          <div className="history-list">
            {history.map((record) => (
              <div key={record.id} className="history-item">
                <span className="history-game">{record.gameId}</span>
                <span className="history-score">{record.score} pts</span>
                <span className="history-date">{formatDate(record.createdAt)}</span>
              </div>
            ))}
          </div>
        </div>
      )}

      <div className="stats-container">
        <div className="stat-card">
          <div className="stat-icon">🏆</div>
          <h3>Achievements</h3>
          <p className="stat-value">{stats.achievements}</p>
          <p className="stat-label">Unlocked</p>
        </div>

        <div className="stat-card">
          <div className="stat-icon">🎯</div>
          <h3>Games Played</h3>
          <p className="stat-value">{stats.gamesPlayed}</p>
          <p className="stat-label">Total Sessions</p>
        </div>

        <div className="stat-card">
          <div className="stat-icon">⭐</div>
          <h3>Level</h3>
          <p className="stat-value">{stats.level}</p>
          <p className="stat-label">Gaming Rank</p>
        </div>
      </div>
    </div>
  );
}

export default UserProfile;
