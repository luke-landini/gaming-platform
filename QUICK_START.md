# 🎮 Gaming Platform - Guida Rapida

## 📂 Struttura Progetto

```
gaming-platform/
├── infra/
│   ├── docker-compose.yml          # PostgreSQL + Keycloak
│   └── KEYCLOAK_SETUP.md          # Guida configurazione Keycloak
└── user-profile-service/
    ├── sql/
    │   └── setup.sql              # Script SQL utili
    ├── src/
    │   ├── main/
    │   │   ├── java/com/gaming/user/
    │   │   │   ├── config/        # SecurityConfig
    │   │   │   ├── controller/    # UserController (REST API)
    │   │   │   ├── dto/          # UserDTO
    │   │   │   ├── entity/       # UserEntity (JPA)
    │   │   │   ├── repository/   # UserRepository
    │   │   │   └── service/      # UserService (business logic)
    │   │   └── resources/
    │   │       ├── application.yml
    │   │       └── schema.sql    # Schema database
    │   └── test/
    │       └── java/com/gaming/user/
    │           ├── integration/  # Test con Testcontainers
    │           └── service/      # Unit test
    ├── pom.xml
    └── README.md
```

## 🚀 Avvio Rapido (5 minuti)

### 1️⃣ Avvia l'infrastruttura

```bash
cd infra
docker-compose up -d

# Verifica che tutto sia running
docker-compose ps
```

**Servizi avviati:**
- PostgreSQL users (porta 5432)
- PostgreSQL keycloak (porta 5433) 
- Keycloak (porta 8080)
- pgAdmin (porta 5050) - opzionale

### 2️⃣ Configura Keycloak

1. Vai su http://localhost:8080/admin
2. Login: `admin` / `admin`
3. Crea realm `myrealm`
4. Crea client `gaming-platform`
5. Crea utente `testuser` con password `password`

**Guida dettagliata**: `infra/KEYCLOAK_SETUP.md`

### 3️⃣ Avvia il microservizio

```bash
cd user-profile-service
mvn spring-boot:run
```

Il servizio sarà disponibile su **http://localhost:8081**

### 4️⃣ Testa l'API

```bash
# 1. Ottieni un token JWT da Keycloak
TOKEN=$(curl -s -X POST http://localhost:8080/realms/myrealm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=gaming-platform" \
  -d "username=testuser" \
  -d "password=password" \
  -d "grant_type=password" | jq -r '.access_token')

# 2. Chiama l'endpoint protetto
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8081/api/v1/users/me
```

**Response attesa:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "test@example.com",
  "username": "testuser",
  "avatarUrl": null,
  "createdAt": "2026-02-04T12:30:00",
  "updatedAt": null
}
```

## 📊 Verifica Database

### Via psql

```bash
# Connettiti al database
docker exec -it gaming-postgres-users psql -U postgres -d gaming_users

# Query utili
SELECT * FROM users;
SELECT COUNT(*) FROM users;
\q
```

### Via pgAdmin

1. Vai su http://localhost:5050
2. Login: `admin@gaming.com` / `admin`
3. Aggiungi server:
   - Nome: `Gaming Users`
   - Host: `postgres-users` (o `host.docker.internal` su Windows)
   - Port: `5432`
   - Database: `gaming_users`
   - Username: `postgres`
   - Password: `luke2001`

## 🧪 Esegui i Test

```bash
cd user-profile-service

# Test unitari
mvn test -Dtest=UserServiceTest

# Test di integrazione (con Testcontainers)
mvn test -Dtest=UserControllerMockMvcTest

# Tutti i test
mvn test
```

## 📡 Endpoints Disponibili

| Endpoint | Metodo | Auth | Descrizione |
|----------|--------|------|-------------|
| `/api/v1/users/me` | GET | JWT | Profilo utente corrente |
| `/actuator/health` | GET | No | Health check |
| `/actuator/info` | GET | No | Informazioni app |

## 🛠️ Comandi Utili

### Docker

```bash
# Stop tutti i servizi
docker-compose down

# Reset completo (cancella anche i dati)
docker-compose down -v

# Logs in tempo reale
docker-compose logs -f

# Restart servizio specifico
docker-compose restart postgres-users
```

### Maven

```bash
# Compila senza test
mvn clean install -DskipTests

# Pulisci build
mvn clean

# Esegui in modalità debug
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Database

```bash
# Backup database
docker exec gaming-postgres-users pg_dump -U postgres gaming_users > backup.sql

# Restore database
cat backup.sql | docker exec -i gaming-postgres-users psql -U postgres gaming_users

# Reset tabella users
docker exec -it gaming-postgres-users psql -U postgres -d gaming_users -c "TRUNCATE TABLE users CASCADE;"
```

## 🏗️ Architettura Implementata

### Pattern e Best Practices

✅ **Layered Architecture**: Controller → Service → Repository  
✅ **DTO Pattern**: Separazione entity/dto  
✅ **Repository Pattern**: Spring Data JPA  
✅ **JWT Resource Server**: Spring Security OAuth2  
✅ **Database per Microservizio**: Isolamento dati  
✅ **Schema SQL Esplicito**: `schema.sql` + `ddl-auto: validate`  
✅ **Transaction Management**: `@Transactional`  
✅ **Idempotency**: Metodo `getOrCreate()`  
✅ **Integration Testing**: Testcontainers  
✅ **Logging**: SLF4J + Lombok  

### Configurazione Security

- **Stateless Sessions**: No cookies, solo JWT
- **CSRF Disabled**: Non necessario per API stateless
- **JWT Validation**: Via JWK Set URI di Keycloak
- **Claim Extraction**: `email` e `preferred_username`

### Gestione Database

- **DDL Mode**: `validate` (production-safe)
- **Schema Init**: `schema.sql` eseguito all'avvio
- **Connection Pool**: HikariCP
- **JPA**: Hibernate con PostgreSQL dialect

## 🐛 Troubleshooting

### Errore: "Cannot connect to database"

```bash
# Verifica che PostgreSQL sia running
docker ps | grep postgres

# Se non è running, avvialo
cd infra
docker-compose up -d postgres-users
```

### Errore: "Invalid JWT"

1. Verifica che Keycloak sia accessibile: http://localhost:8080
2. Controlla che il realm sia `myrealm`
3. Rigenera un nuovo token (potrebbero essere scaduti dopo 5 minuti)

### Errore: "Table 'users' doesn't exist"

```bash
# Verifica che schema.sql sia nella cartella resources
ls user-profile-service/src/main/resources/schema.sql

# Forza ricreazione
docker exec -it gaming-postgres-users psql -U postgres -d gaming_users < user-profile-service/src/main/resources/schema.sql
```

### Il servizio si avvia ma non risponde

```bash
# Verifica i log
cd user-profile-service
mvn spring-boot:run

# Se vedi errori di connessione al DB, verifica:
docker-compose ps
docker-compose logs postgres-users
```

## 📝 Prossimi Step

1. **Implementa altri endpoint**:
   - `PATCH /api/v1/users/me` - Update profilo
   - `POST /api/v1/users/me/avatar` - Upload avatar

2. **Aggiungi altri microservizi**:
   - `game-service` (porta 8082)
   - `match-service` (porta 8083)
   - `leaderboard-service` (porta 8084)

3. **Migliora l'infrastruttura**:
   - API Gateway (Spring Cloud Gateway)
   - Service Discovery (Eureka)
   - Config Server
   - Redis per caching

4. **Implementa CI/CD**:
   - GitHub Actions
   - Docker images
   - Kubernetes deployment

## 📚 Documentazione

- **User Profile Service**: `user-profile-service/README.md`
- **Keycloak Setup**: `infra/KEYCLOAK_SETUP.md`
- **SQL Scripts**: `user-profile-service/sql/setup.sql`

## 🤝 Supporto

Per domande o problemi:
1. Controlla i log: `docker-compose logs -f`
2. Verifica health check: `curl http://localhost:8081/actuator/health`
3. Consulta la documentazione nei file README

---

**Buon coding! 🚀**
