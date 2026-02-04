import { useState, useEffect } from 'react';
import { userApi } from '../services/api';
import './UserProfile.css';

function UserProfile({ onLogout }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchUserProfile();
  }, []);

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
      <div className="profile-header">
        <h1>🎮 Gaming Platform</h1>
        <button onClick={handleLogout} className="btn-logout">
          Logout
        </button>
      </div>

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
            <button onClick={fetchUserProfile} className="btn btn-primary">
              🔄 Refresh Profile
            </button>
          </div>
        </div>
      </div>

      <div className="stats-container">
        <div className="stat-card">
          <div className="stat-icon">🏆</div>
          <h3>Achievements</h3>
          <p className="stat-value">0</p>
          <p className="stat-label">Coming soon</p>
        </div>

        <div className="stat-card">
          <div className="stat-icon">🎯</div>
          <h3>Games Played</h3>
          <p className="stat-value">0</p>
          <p className="stat-label">Coming soon</p>
        </div>

        <div className="stat-card">
          <div className="stat-icon">⭐</div>
          <h3>Level</h3>
          <p className="stat-value">1</p>
          <p className="stat-label">Coming soon</p>
        </div>
      </div>
    </div>
  );
}

export default UserProfile;
