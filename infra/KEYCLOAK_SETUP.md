# Configurazione Keycloak per Gaming Platform

## 🚀 Avvio Rapido

```bash
# Avvia tutti i servizi
cd infra
docker-compose up -d

# Verifica che siano running
docker-compose ps

# Logs
docker-compose logs -f keycloak
```

## 🔐 Accesso Keycloak

- **URL**: http://localhost:8080
- **Admin Console**: http://localhost:8080/admin
- **Username**: `admin`
- **Password**: `admin`

## ⚙️ Configurazione Realm

### 1. Crea un nuovo Realm

1. Accedi a Keycloak Admin Console
2. Click su **"Create Realm"** (dropdown in alto a sinistra)
3. Nome: `myrealm`
4. Click **"Create"**

### 2. Crea un Client

1. Nel realm `myrealm`, vai su **Clients** → **Create client**
2. Configurazione:
   - **Client ID**: `gaming-platform`
   - **Client Protocol**: `openid-connect`
   - **Root URL**: `http://localhost:8081`
3. Click **Next**
4. Abilita:
   - ✅ **Standard flow**
   - ✅ **Direct access grants**
   - ✅ **Service accounts roles**
5. Click **Next** → **Save**

### 3. Configura il Client

1. Nella tab **Settings** del client:
   - **Access Type**: `confidential` (se vuoi client secret)
   - **Valid Redirect URIs**: `http://localhost:8081/*`
   - **Web Origins**: `http://localhost:8081`
2. Salva

3. Vai alla tab **Credentials**:
   - Copia il **Client Secret** (ti servirà per ottenere i token)

### 4. Crea un Utente Test

1. Vai su **Users** → **Add user**
2. Compila:
   - **Username**: `testuser`
   - **Email**: `test@example.com`
   - **Email Verified**: `ON`
   - **First Name**: `Test`
   - **Last Name**: `User`
3. Click **Create**

4. Nella tab **Credentials**:
   - Click **Set password**
   - Password: `password`
   - Disabilita **Temporary**
   - Click **Save**

### 5. Configura i Claim JWT

Per assicurarti che `email` e `preferred_username` siano nel token:

1. Vai su **Client Scopes** → **profile**
2. Tab **Mappers** → verifica che ci siano:
   - `username` mapper (mappa a `preferred_username`)
   - `email` mapper

Oppure crea un mapper custom:

1. **Clients** → `gaming-platform` → **Client scopes** tab
2. Click su **gaming-platform-dedicated**
3. Tab **Mappers** → **Add mapper** → **By configuration**
4. Scegli **User Attribute**:
   - **Name**: `email`
   - **User Attribute**: `email`
   - **Token Claim Name**: `email`
   - **Claim JSON Type**: `String`
   - **Add to ID token**: `ON`
   - **Add to access token**: `ON`
   - **Add to userinfo**: `ON`

## 🧪 Test dei Token

### Ottieni un Access Token (Password Grant)

```bash
curl -X POST http://localhost:8080/realms/myrealm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=gaming-platform" \
  -d "client_secret=YOUR_CLIENT_SECRET" \
  -d "username=testuser" \
  -d "password=password" \
  -d "grant_type=password"
```

**Response:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI...",
  "token_type": "Bearer"
}
```

### Decodifica il Token

Usa https://jwt.io per decodificare e verificare i claim:

```json
{
  "exp": 1234567890,
  "iat": 1234567590,
  "jti": "uuid-here",
  "iss": "http://localhost:8080/realms/myrealm",
  "sub": "user-uuid",
  "typ": "Bearer",
  "azp": "gaming-platform",
  "email": "test@example.com",
  "preferred_username": "testuser",
  "email_verified": true
}
```

### Test con il Microservizio

```bash
# Salva il token in una variabile
TOKEN="eyJhbGciOiJSUzI1NiIsInR5cCI..."

# Chiama l'endpoint protetto
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8081/api/v1/users/me
```

## 🔄 Script Automatico (Windows PowerShell)

```powershell
# get-token.ps1
$response = Invoke-RestMethod -Uri "http://localhost:8080/realms/myrealm/protocol/openid-connect/token" `
  -Method Post `
  -ContentType "application/x-www-form-urlencoded" `
  -Body @{
    client_id = "gaming-platform"
    client_secret = "YOUR_CLIENT_SECRET"
    username = "testuser"
    password = "password"
    grant_type = "password"
  }

$token = $response.access_token
Write-Host "Token: $token"

# Test endpoint
$headers = @{
  Authorization = "Bearer $token"
}

Invoke-RestMethod -Uri "http://localhost:8081/api/v1/users/me" `
  -Method Get `
  -Headers $headers
```

## 🔄 Script Automatico (Linux/Mac Bash)

```bash
#!/bin/bash
# get-token.sh

# Ottieni il token
RESPONSE=$(curl -s -X POST http://localhost:8080/realms/myrealm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=gaming-platform" \
  -d "client_secret=YOUR_CLIENT_SECRET" \
  -d "username=testuser" \
  -d "password=password" \
  -d "grant_type=password")

TOKEN=$(echo $RESPONSE | jq -r '.access_token')

echo "Token: $TOKEN"
echo ""

# Test endpoint
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8081/api/v1/users/me | jq
```

## 🐳 Comandi Docker Utili

```bash
# Stop tutti i servizi
docker-compose down

# Stop e rimuovi volumi (reset completo)
docker-compose down -v

# Riavvia solo Keycloak
docker-compose restart keycloak

# Logs in real-time
docker-compose logs -f keycloak

# Accedi al container PostgreSQL
docker exec -it gaming-postgres-users psql -U postgres -d gaming_users
```

## 🔧 Troubleshooting

### Keycloak non si avvia

**Errore**: `Connection refused` al database

**Soluzione**:
```bash
docker-compose down
docker-compose up -d postgres-keycloak
# Aspetta 10 secondi
docker-compose up -d keycloak
```

### Token non valido

**Errore**: `Invalid JWT signature`

**Causa**: Il JWK endpoint non corrisponde

**Soluzione**:
- Verifica che in `application.yml` ci sia:
  ```yaml
  spring:
    security:
      oauth2:
        resourceserver:
          jwt:
            jwk-set-uri: http://localhost:8080/realms/myrealm/protocol/openid-connect/certs
  ```
- Assicurati che il realm sia `myrealm`

### Email/username non nel token

**Soluzione**: Verifica i mapper del client come descritto sopra nella sezione "Configura i Claim JWT"

## 📚 Risorse

- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Spring Security OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [JWT.io - Token Debugger](https://jwt.io)
