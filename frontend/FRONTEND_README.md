# Gaming Platform - Frontend

Interfaccia utente per il microservizio user-profile-service.

## 🎯 Caratteristiche

- **Autenticazione JWT**: Login tramite Keycloak con supporto OAuth2
- **Gestione Profilo Utente**: Visualizzazione dati profilo con auto-creazione
- **Design Moderno**: Interfaccia minimale ma professionale con gradients e animazioni
- **Responsive**: Funziona su desktop, tablet e mobile
- **Gestione Errori**: Feedback chiaro su errori e stati di caricamento

## 📋 Prerequisiti

- Node.js (v18 o superiore)
- Backend user-profile-service attivo su `http://localhost:8081`
- Keycloak attivo su `http://localhost:8080`

## 🚀 Come Avviare

1. **Installa le dipendenze** (se non già fatto):
   ```bash
   cd frontend
   npm install
   ```

2. **Avvia il dev server**:
   ```bash
   npm run dev
   ```

3. **Apri il browser** su `http://localhost:5173`

## 🧪 Come Testare

### Test Completo del Flusso

1. **Avvia tutti i servizi necessari**:
   ```bash
   # Terminale 1: Avvia Keycloak e PostgreSQL
   cd infra
   docker-compose up
   
   # Terminale 2: Avvia il backend
   cd user-profile-service
   mvn spring-boot:run
   
   # Terminale 3: Avvia il frontend
   cd frontend
   npm run dev
   ```

2. **Configura Keycloak** (se non già fatto):
   - Accedi a Keycloak Admin Console: `http://localhost:8080`
   - Username: `admin`, Password: `admin`
   - Crea un realm chiamato `myrealm`
   - Crea un client chiamato `gaming-frontend`:
     - Client authentication: OFF
     - Direct access grants: ON (per test con password grant)
     - Valid redirect URIs: `http://localhost:5173/*`
     - Web origins: `http://localhost:5173`
   - Crea un utente di test con email e password

3. **Testa il login**:
   - Apri `http://localhost:5173`
   - Inserisci le credenziali dell'utente creato
   - Dovresti vedere il profilo utente caricato

### Test dei Componenti

#### Test del Login
- ✅ Inserisci credenziali valide → Dovresti vedere il profilo
- ✅ Inserisci credenziali invalide → Dovresti vedere errore "Invalid credentials"
- ✅ Clicca "Sign in with Keycloak SSO" → Redirect alla pagina login Keycloak

#### Test del Profilo
- ✅ Il profilo mostra username, email, ID
- ✅ Mostra "Member Since" e "Last Updated" con date corrette
- ✅ L'avatar mostra le iniziali se non c'è avatarUrl
- ✅ Clicca "Refresh Profile" → Ricarica i dati
- ✅ Clicca "Logout" → Torna alla pagina login

#### Test Gestione Errori
- ✅ Spegni il backend → Dovresti vedere errore con pulsante "Retry"
- ✅ Token scaduto/invalido → Redirect automatico al login dopo 2 secondi

## 🏗️ Struttura del Progetto

```
frontend/
├── src/
│   ├── components/
│   │   ├── Login.jsx           # Componente login con Keycloak
│   │   ├── Login.css           # Stili login
│   │   ├── UserProfile.jsx     # Componente profilo utente
│   │   └── UserProfile.css     # Stili profilo
│   ├── services/
│   │   └── api.js             # Client HTTP con Axios e interceptors
│   ├── App.jsx                # Componente principale con routing auth
│   ├── App.css                # Stili globali app
│   └── main.jsx               # Entry point React
└── package.json               # Dipendenze e scripts
```

## 🎨 Design System

### Colori Principali
- **Primary Gradient**: `#667eea` → `#764ba2`
- **Background**: Gradient viola-blu
- **Text**: `#1a202c` (dark), `#718096` (gray)
- **Success**: `#48bb78` (green)
- **Error**: `#c53030` (red)

### Componenti UI
- **Cards**: Border-radius 16px, shadow elevato
- **Buttons**: Hover effect con translateY e shadow
- **Inputs**: Border 2px, focus ring colorato
- **Avatar**: Circular 120px con gradient se no image

## 🔧 Configurazione

### Modifica URL Backend
Edita `src/services/api.js`:
```javascript
const API_BASE_URL = 'http://localhost:8081/api/v1';
```

### Modifica Configurazione Keycloak
Edita `src/components/Login.jsx`:
```javascript
const KEYCLOAK_URL = 'http://localhost:8080';
const REALM = 'myrealm';
const CLIENT_ID = 'gaming-frontend';
```

## 📦 Build per Produzione

```bash
npm run build
```

I file ottimizzati saranno in `dist/`. Puoi servirli con qualsiasi web server.

Preview della build:
```bash
npm run preview
```

## 🔐 Sicurezza

- **JWT Storage**: Token salvati in localStorage (per sviluppo)
  - ⚠️ In produzione considera httpOnly cookies
- **CORS**: Configurato nel backend per accettare `http://localhost:5173`
- **Interceptors**: Auto-logout su 401 Unauthorized
- **Validation**: Input validati lato client e server

## 🛠️ Tecnologie Usate

- **React 19**: UI library
- **Vite**: Build tool e dev server
- **Axios**: HTTP client
- **CSS3**: Styling con modern features (grid, flexbox, animations)
- **Keycloak**: Identity provider (OAuth2/OIDC)

## 📝 API Endpoints Usati

| Endpoint | Metodo | Descrizione |
|----------|--------|-------------|
| `/api/v1/users/me` | GET | Ottieni profilo utente corrente |

## 🐛 Troubleshooting

### Login fallisce con errore CORS
- Verifica che il backend abbia CORS configurato per `http://localhost:5173`
- Controlla la console del browser per errori specifici

### "Failed to load user profile"
- Verifica che il backend sia attivo su `http://localhost:8081`
- Controlla che il token JWT sia valido
- Verifica i log del backend per errori

### Keycloak non risponde
- Verifica che Keycloak sia attivo: `http://localhost:8080`
- Controlla i log di docker-compose
- Verifica che il realm e il client siano configurati correttamente

## 🚀 Prossimi Sviluppi

- [ ] Refresh token automatico
- [ ] Edit profilo (update username, avatar)
- [ ] Upload immagine avatar
- [ ] Statistiche reali (games played, achievements)
- [ ] Dark mode toggle
- [ ] Notifiche toast
- [ ] Testing con Jest + React Testing Library
