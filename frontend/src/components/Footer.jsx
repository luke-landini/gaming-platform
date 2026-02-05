import './Footer.css';

function Footer() {
  const companyAddress = "Via delle Industrie, 123, 00100 Roma RM, Italy";
  const encodedAddress = encodeURIComponent(companyAddress);
  const publicMapUrl = `https://www.google.com/maps?q=${encodedAddress}&output=embed`;

  return (
    <footer className="footer">
      <div className="footer-top-gradient"></div>
      <div className="footer-content">
        <div className="footer-section about">
          <div className="footer-logo">🎮 Gaming Platform</div>
          <p className="about-text">
            Elevate your gaming experience with our curated selection of legendary titles. 
            Join a global community of players and dominate the leaderboards.
          </p>
          <div className="social-links">
            <a href="#" className="social-card discord">
              <span className="social-icon-wrapper">🎮</span>
              <span className="social-name">Discord</span>
            </a>
            <a href="#" className="social-card twitch">
              <span className="social-icon-wrapper">📺</span>
              <span className="social-name">Twitch</span>
            </a>
            <a href="#" className="social-card twitter">
              <span className="social-icon-wrapper">🐦</span>
              <span className="social-name">Twitter</span>
            </a>
          </div>
        </div>
        
        <div className="footer-section links">
          <h3>Quick Links</h3>
          <ul>
            <li><a href="/">Home</a></li>
            <li><a href="/catalog">Game Catalog</a></li>
            <li><a href="/profile">User Profile</a></li>
          </ul>
        </div>

        <div className="footer-section location">
          <h3>Our Location</h3>
          <div className="map-container">
            <iframe
              title="Company Location"
              src={publicMapUrl}
              allowFullScreen=""
              loading="lazy"
              referrerPolicy="no-referrer-when-downgrade"
            ></iframe>
          </div>
        </div>
      </div>

      <div className="footer-bottom">
        <div className="footer-bottom-content">
          <p>© 2026 Gaming Platform. All rights reserved.</p>
          <div className="legal-links">
            <a href="#">Terms of Service</a>
            <span className="separator">|</span>
            <a href="#">Privacy Policy</a>
            <span className="separator">|</span>
            <a href="#">Contact Us</a>
          </div>
        </div>
      </div>
    </footer>
  );
}

export default Footer;
