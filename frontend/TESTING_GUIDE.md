# 🧪 Guida Completa al Testing - Gaming Platform Frontend

## 📋 Indice

1. [Setup Iniziale](#setup-iniziale)
2. [Test Manuale Completo](#test-manuale-completo)
3. [Test Scenari Specifici](#test-scenari-specifici)
4. [Troubleshooting](#troubleshooting)
5. [Checklist di Test](#checklist-di-test)

---

## 🚀 Setup Iniziale

### Passo 1: Avvia Keycloak e PostgreSQL

```bash
cd C:\Users\llandini\gaming-platform\infra
docker-compose up
```

**Cosa aspettarsi:**
- Keycloak disponibile su `http://localhost:8080`
- PostgreSQL disponibile su `localhost:5432`
- Attendi che i log mostrino "Keycloak started"

### Passo 2: Configura Keycloak (Prima volta)

1. **Accedi all'Admin Console:**
   - URL: `http://localhost:8080`
   - Username: `admin`
   - Password: `admin`

2. **Crea il Realm:**
   - Clicca su "master" in alto a sinistra
   - Clicca "Create Realm"
   - Nome: `myrealm`
   - Clicca "Create"

3. **Crea il Client:**
   - Nel realm `myrealm`, vai su "Clients"
   - Clicca "Create client"
   - Client ID: `gaming-frontend`
   - Clicca "Next"
   - **Client authentication: OFF** ✅
   - **Authorization: OFF** ✅
   - **Authentication flow:**
     - ✅ Standard flow
     - ✅ Direct access grants (importante!)
     - ❌ Implicit flow
   - Clicca "Next"
   - **Valid redirect URIs:**
     - `http://localhost:5173/*`
   - **Web origins:**
     - `http://localhost:5173`
   - Clicca "Save"

4. **Crea un Utente di Test:**
   - Vai su "Users"
   - Clicca "Add user"
   - **Username:** `testuser`
   - **Email:** `test@example.com`
   - **Email verified:** ✅ ON
   - **First name:** `Test`
   - **Last name:** `User`
   - Clicca "Create"
   
5. **Imposta la Password:**
   - Vai alla tab "Credentials"
   - Clicca "Set password"
   - **Password:** `password123`
   - **Password confirmation:** `password123`
   - **Temporary:** ❌ OFF (importante!)
   - Clicca "Save"

### Passo 3: Avvia il Backend

```bash
cd C:\Users\llandini\gaming-platform\user-profile-service
mvn spring-boot:run
```

**Cosa aspettarsi:**
- Backend disponibile su `http://localhost:8081`
- Log dovrebbero mostrare "Started UserProfileServiceApplication"
- Nessun errore di connessione a PostgreSQL o Keycloak

### Passo 4: Avvia il Frontend

```bash
cd C:\Users\llandini\gaming-platform\frontend
npm run dev
```

**Cosa aspettarsi:**
- Frontend disponibile su `http://localhost:5173`
- Browser dovrebbe aprirsi automaticamente

---

## 🧪 Test Manuale Completo

### Test 1: Flusso di Login Base ✅

**Obiettivo:** Verificare che il login funzioni correttamente

**Passi:**
1. Apri `http://localhost:5173`
2. Dovresti vedere la pagina di login con:
   - Logo "🎮 Gaming Platform"
   - Due campi: Email/Username e Password
   - Pulsante "Sign In"
   - Pulsante "Sign in with Keycloak SSO"

3. **Inserisci credenziali valide:**
   - Email: `test@example.com` o `testuser`
   - Password: `password123`
   - Clicca "Sign In"

**Risultato Atteso:**
- ✅ Pulsante mostra "Signing in..." durante il caricamento
- ✅ Dopo 1-2 secondi, vedi la pagina del profilo
- ✅ Console del browser NON mostra errori

**Verifiche Profilo:**
- ✅ Username mostrato: `testuser`
- ✅ Email mostrata: `test@example.com`
- ✅ ID UUID valido mostrato
- ✅ Date "Member Since" e "Last Updated" presenti
- ✅ Avatar con iniziali "TE"
- ✅ Badge "Active" verde visibile

---

### Test 2: Gestione Errori Login ❌

**Obiettivo:** Verificare che gli errori siano gestiti correttamente

**Test 2.1: Credenziali Invalide**

**Passi:**
1. Nella pagina di login, inserisci:
   - Email: `wrong@example.com`
   - Password: `wrongpassword`
2. Clicca "Sign In"

**Risultato Atteso:**
- ❌ Messaggio errore rosso: "Invalid credentials"
- ❌ Rimani sulla pagina di login
- ✅ Campi rimangono compilati

**Test 2.2: Campi Vuoti**

**Passi:**
1. Lascia campi vuoti
2. Clicca "Sign In"

**Risultato Atteso:**
- ❌ Browser mostra validazione HTML5 "Please fill out this field"
- ❌ Form non inviato

---

### Test 3: Interazione Profilo Utente 🔄

**Obiettivo:** Testare le funzionalità della pagina profilo

**Test 3.1: Refresh Profile**

**Passi:**
1. Login con successo
2. Clicca pulsante "🔄 Refresh Profile"

**Risultato Atteso:**
- ✅ Spinner di caricamento appare brevemente
- ✅ Dati ricaricati (verifica che "Last Updated" potrebbe cambiare)
- ✅ Nessun errore

**Test 3.2: Logout**

**Passi:**
1. Nella pagina profilo, clicca "Logout" in alto a destra

**Risultato Atteso:**
- ✅ Ritorno immediato alla pagina login
- ✅ Token rimossi da localStorage (verifica in DevTools → Application → Local Storage)
- ✅ Se provi a tornare indietro nel browser, vieni rediretto al login

---

### Test 4: Persistenza Sessione 💾

**Obiettivo:** Verificare che la sessione persista al refresh

**Passi:**
1. Login con successo
2. Sei nella pagina profilo
3. Premi F5 (refresh pagina)

**Risultato Atteso:**
- ✅ Breve spinner "Loading..."
- ✅ Profilo ricaricato automaticamente
- ✅ NON richiesto nuovo login

**Test con Token Scaduto:**
1. Login con successo
2. Apri DevTools → Console
3. Esegui: `localStorage.setItem('access_token', 'invalid_token')`
4. Premi F5

**Risultato Atteso:**
- ❌ Errore "Failed to load user profile"
- ⏱️ Dopo 2 secondi, redirect al login
- ✅ Token invalido rimosso

---

### Test 5: Responsive Design 📱

**Obiettivo:** Verificare il design su diverse dimensioni

**Passi:**
1. Login con successo
2. Apri DevTools (F12)
3. Attiva Device Toolbar (Ctrl+Shift+M)
4. Prova diverse dimensioni:
   - Mobile: 375x667 (iPhone SE)
   - Tablet: 768x1024 (iPad)
   - Desktop: 1920x1080

**Risultato Atteso Mobile:**
- ✅ Card profilo occupa tutta la larghezza
- ✅ Stats cards impilate verticalmente
- ✅ Font ridimensionati appropriatamente
- ✅ Pulsanti touch-friendly

---

### Test 6: Network Conditions 🌐

**Obiettivo:** Testare con backend offline

**Test 6.1: Backend Down**

**Passi:**
1. Login con successo
2. Ferma il backend (Ctrl+C nel terminale backend)
3. Nella pagina profilo, clicca "🔄 Refresh Profile"

**Risultato Atteso:**
- ⚠️ Icona errore "⚠️"
- ❌ Messaggio "Error Loading Profile"
- ❌ Dettaglio errore: "Failed to load user profile"
- ✅ Pulsante "Retry" disponibile
- ✅ Pulsante "Back to Login" disponibile

**Test 6.2: Recovery**

**Passi:**
1. Riavvia il backend: `mvn spring-boot:run`
2. Clicca "Retry"

**Risultato Atteso:**
- ✅ Profilo ricaricato con successo

---

### Test 7: OAuth2 SSO Flow 🔐

**Obiettivo:** Testare il flusso OAuth2 standard

**Passi:**
1. Nella pagina login, clicca "Sign in with Keycloak SSO"

**Risultato Atteso:**
- ✅ Redirect alla pagina login Keycloak
- ✅ URL contiene parametri: `client_id=gaming-frontend`, `response_type=code`

**Nota:** Il flusso completo OAuth2 richiede configurazione aggiuntiva del backend per gestire il callback. Per ora, usa il login diretto con username/password.

---

## 🎨 Test Visivi

### Test 8: Animazioni e Transizioni ✨

**Cosa verificare:**

**Login Page:**
- ✅ Card slide-up animation all'apertura
- ✅ Input focus → border blu + shadow
- ✅ Button hover → lift effect + shadow
- ✅ Error message → fade in con border rosso

**Profile Page:**
- ✅ Banner gradient animato (shift colori ogni 8s)
- ✅ Hover su detail cards → lift + border blu
- ✅ Hover su stat cards → lift + shadow aumentato
- ✅ Spinner rotazione fluida durante loading

**Colori:**
- ✅ Gradient principale: viola (#667eea) → viola scuro (#764ba2)
- ✅ Testo principale: dark (#1a202c)
- ✅ Testo secondario: gray (#718096)
- ✅ Success badge: verde (#48bb78)

---

## 🔍 Checklist di Test

### ✅ Funzionalità Base
- [ ] Login con credenziali valide
- [ ] Errore con credenziali invalide
- [ ] Visualizzazione profilo utente
- [ ] Refresh profilo
- [ ] Logout
- [ ] Persistenza sessione dopo refresh

### ✅ Gestione Errori
- [ ] Backend offline → Messaggio errore
- [ ] Token invalido → Redirect login
- [ ] Network error → Retry disponibile
- [ ] Campi vuoti → Validazione form

### ✅ UI/UX
- [ ] Animazioni fluide
- [ ] Responsive mobile/tablet/desktop
- [ ] Loading states chiari
- [ ] Hover effects funzionanti
- [ ] Colori e typography corretti

### ✅ Sicurezza
- [ ] Token JWT inviato in Authorization header
- [ ] Token rimosso al logout
- [ ] CORS configurato correttamente
- [ ] 401 → Auto logout

---

## 🐛 Troubleshooting

### Problema: "CORS policy error"

**Sintomo:** Console mostra errore CORS

**Soluzione:**
1. Verifica che il backend sia avviato
2. Controlla SecurityConfig.java → corsConfigurationSource()
3. Verifica che `http://localhost:5173` sia negli allowed origins
4. Riavvia il backend

---

### Problema: "Invalid credentials" con credenziali corrette

**Possibili cause:**

1. **Client Keycloak non configurato correttamente:**
   - Verifica "Direct access grants" sia ENABLED
   - Verifica Client authentication sia OFF

2. **Password temporanea:**
   - In Keycloak, vai su Users → testuser → Credentials
   - Verifica che "Temporary" sia OFF

3. **Realm sbagliato:**
   - Verifica che l'utente sia nel realm `myrealm`
   - Verifica che il client sia nel realm `myrealm`

---

### Problema: "Failed to load user profile"

**Possibili cause:**

1. **Backend non raggiungibile:**
   ```bash
   curl http://localhost:8081/actuator/health
   ```
   Se errore → Backend non attivo

2. **Database non connesso:**
   - Verifica che PostgreSQL sia attivo
   - Controlla log backend per errori database

3. **Keycloak JWK endpoint non raggiungibile:**
   ```bash
   curl http://localhost:8080/realms/myrealm/protocol/openid-connect/certs
   ```
   Se errore → Keycloak non attivo

---

### Problema: Token non accettato dal backend

**Debug:**

1. **Copia il token dal localStorage:**
   - DevTools → Application → Local Storage → access_token

2. **Decodifica il token:**
   - Vai su https://jwt.io
   - Incolla il token
   - Verifica claims: email, preferred_username, iss, aud

3. **Verifica claims richiesti:**
   - Deve avere: `email`, `preferred_username`
   - Issuer deve corrispondere a Keycloak

---

## 📊 Metriche di Successo

### Performance
- ⏱️ Login < 2 secondi
- ⏱️ Caricamento profilo < 1 secondo
- ⏱️ Refresh profilo < 1 secondo

### Affidabilità
- ✅ 0 errori console in happy path
- ✅ Gestione graceful di tutti gli errori
- ✅ Recovery automatico dove possibile

### UX
- ✅ Feedback chiaro per ogni azione
- ✅ Stati di loading visibili
- ✅ Messaggi errore comprensibili
- ✅ Animazioni fluide (no lag)

---

## 🎓 Alternative di Design

### Alternative Implementate vs Non Implementate

#### 1. **Gestione Autenticazione**

**✅ Scelta Attuale: Password Grant + OAuth2**
- Pro: Flessibile, supporta entrambi i flussi
- Pro: Facile da testare in sviluppo
- Con: Password grant deprecato in OAuth2.1

**❌ Alternative Non Implementate:**
- **Solo OAuth2 Authorization Code:**
  - Pro: Più sicuro, standard moderno
  - Con: Richiede backend route per callback
  - Con: Più complesso da testare

- **Session-based Auth:**
  - Pro: Più semplice per applicazioni monolitiche
  - Con: Non stateless, problemi con microservizi

#### 2. **State Management**

**✅ Scelta Attuale: React useState/useEffect**
- Pro: Semplice per app piccola
- Pro: No dependencies extra
- Con: Difficile scalare con più componenti

**❌ Alternative Non Implementate:**
- **Redux / Zustand:**
  - Pro: Stato globale centralizzato
  - Pro: DevTools per debugging
  - Con: Overkill per 2 componenti

- **React Context:**
  - Pro: Built-in React, no dependencies
  - Pro: Buono per tema, auth state
  - Con: Re-render tutto il tree

#### 3. **Styling**

**✅ Scelta Attuale: CSS puro + BEM-like naming**
- Pro: No build step extra, performance ottimale
- Pro: Pieno controllo, animazioni custom
- Con: Verboso, può essere ripetitivo

**❌ Alternative Non Implementate:**
- **Tailwind CSS:**
  - Pro: Utility-first, veloce sviluppo
  - Pro: Tema consistente
  - Con: HTML verbose, curva apprendimento

- **Styled Components / Emotion:**
  - Pro: CSS-in-JS, scope automatico
  - Pro: Props-based styling
  - Con: Runtime overhead, SSR complesso

- **Material-UI / Ant Design:**
  - Pro: Componenti pronti all'uso
  - Pro: Accessibilità integrata
  - Con: Bundle size grande, styling rigid

#### 4. **HTTP Client**

**✅ Scelta Attuale: Axios**
- Pro: Interceptors built-in, API pulita
- Pro: Timeout, retry facili
- Con: Dependency extra (minore)

**❌ Alternative Non Implementate:**
- **Fetch API:**
  - Pro: Built-in browser, no dependencies
  - Con: Verboso, interceptors manuali

- **React Query / SWR:**
  - Pro: Caching automatico, refetch
  - Pro: Loading/error states automatici
  - Con: Overkill per pochi endpoints

#### 5. **Routing**

**✅ Scelta Attuale: Conditional Rendering**
- Pro: Semplicissimo per 2 pagine
- Pro: No router dependency
- Con: Non scala con più pagine

**❌ Alternative Non Implementate:**
- **React Router:**
  - Pro: URL-based navigation, history
  - Pro: Nested routes, code splitting
  - Con: Non necessario per questa app

---

## 🚀 Comandi Quick Reference

```bash
# Avvia tutto
cd infra && docker-compose up -d
cd ../user-profile-service && mvn spring-boot:run &
cd ../frontend && npm run dev

# Stop tutto
# Ctrl+C nei terminali
cd infra && docker-compose down

# Reset completo database
cd infra
docker-compose down -v
docker-compose up

# Clean frontend
cd frontend
rm -rf node_modules package-lock.json
npm install

# Build frontend per produzione
cd frontend
npm run build
npm run preview
```

---

## 📚 Risorse Utili

- **JWT Debugger:** https://jwt.io
- **Keycloak Docs:** https://www.keycloak.org/docs/latest/
- **React DevTools:** Chrome Extension
- **Axios Docs:** https://axios-http.com/docs/intro

---

✅ **Test completato con successo quando tutti i checkmarks sono verdi!**
