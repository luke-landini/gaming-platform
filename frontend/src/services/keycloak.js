import Keycloak from 'keycloak-js';

// Keycloak configuration
const keycloak = new Keycloak({
  url: 'http://localhost:8080',
  realm: 'myrealm',
  clientId: 'gaming-frontend',
});

export default keycloak;
