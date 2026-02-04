# User Profile Service

Microservizio Spring Boot per la gestione dei profili utente con autenticazione JWT.

## 📋 Tecnologie

- **Java 17**
- **Spring Boot 3.2.2**
- **Spring Security** (OAuth2 Resource Server)
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **Testcontainers** (per integration testing)

## 🏗️ Architettura

```
src/main/java/com/gaming/user/
├── UserProfileServiceApplication.java  # Main class
├── config/
│   └── SecurityConfig.java            # Configurazione JWT
├── controller/
│   └── UserController.java            # REST endpoints
├── dto/
│   └── UserDTO.java                   # Data Transfer Object
├── entity/
│   └── UserEntity.java                # JPA Entity
├── repository/
│   └── UserRepository.java            # Spring Data Repository
└── service/
    └── UserService.java               # Business logic
```

## 🚀 Setup

### 1. Crea il Database PostgreSQL

```bash
# Accedi a PostgreSQL
psql -U postgres

# Crea il database
CREATE DATABASE gaming_users;

# Verifica
\l

# Esci
\q
```

### 2. Configura application.yml

Il file è già configurato per connettersi a:
- **Database**: `gaming_users`
- **User**: `postgres`
- **Password**: `luke2001`
- **JWK URI**: `http://localhost:8080/realms/myrealm/protocol/openid-connect/certs`

Modifica `src/main/resources/application.yml` se necessario.

### 3. Avvia l'applicazione

```bash
# Compila ed esegui
mvn spring-boot:run

# Oppure
mvn clean package
java -jar target/user-profile-service-1.0-SNAPSHOT.jar
```

## 📊 Schema Database

Lo schema viene creato automaticamente all'avvio tramite `schema.sql`:

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

**Nota**: Hibernate è configurato con `ddl-auto: validate`, quindi verifica solo che lo schema corrisponda alle entity senza modificare il database.

## 🔐 Autenticazione JWT

Il servizio si aspetta un JWT con questi claim:

- **email** (obbligatorio): Email dell'utente
- **preferred_username** (opzionale): Username dell'utente

Esempio JWT payload:
```json
{
  "email": "user@example.com",
  "preferred_username": "johndoe",
  "exp": 1234567890
}
```

## 📡 API Endpoints

### GET `/api/v1/users/me`

Ottiene il profilo dell'utente autenticato. Se l'utente non esiste, viene creato automaticamente.

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response 200 OK:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "username": "johndoe",
  "avatarUrl": null,
  "createdAt": "2026-02-04T10:30:00",
  "updatedAt": null
}
```

**Response 401 Unauthorized:**
```json
{
  "error": "Unauthorized"
}
```

**Response 400 Bad Request:**
```json
{
  "error": "Missing required JWT claim: email"
}
```

## 🧪 Testing

### Test Unitari

```bash
mvn test -Dtest=UserServiceTest
```

### Test di Integrazione con Testcontainers

```bash
mvn test -Dtest=UserControllerMockMvcTest
```

I test utilizzano Testcontainers per avviare un PostgreSQL container temporaneo.

### Test Manuale con cURL

1. **Ottieni un JWT da Keycloak** (assumi di avere Keycloak running su porta 8080)

```bash
# Login e ottieni token
TOKEN=$(curl -X POST http://localhost:8080/realms/myrealm/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=testuser" \
  -d "password=password" \
  -d "grant_type=password" \
  -d "client_id=your-client" | jq -r '.access_token')
```

2. **Chiama l'endpoint protetto**

```bash
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8081/api/v1/users/me
```

3. **Health Check (senza autenticazione)**

```bash
curl http://localhost:8081/actuator/health
```

## 🔧 Configurazione

### Profile di Sviluppo

Per usare configurazioni diverse in test:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Logging

Livelli di log configurabili in `application.yml`:

```yaml
logging:
  level:
    com.gaming.user: DEBUG          # Log del nostro codice
    org.springframework.security: DEBUG  # Log di Spring Security
    org.hibernate.SQL: DEBUG        # Log delle query SQL
```

## 📦 Build

### Crea un JAR eseguibile

```bash
mvn clean package

# Il JAR sarà in target/user-profile-service-1.0-SNAPSHOT.jar
```

### Docker (Opzionale)

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/user-profile-service-1.0-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 🐛 Troubleshooting

### Errore: "Cannot create PoolableConnectionFactory"

**Causa**: PostgreSQL non è raggiungibile o il database non esiste.

**Soluzione**:
```bash
psql -U postgres -c "CREATE DATABASE gaming_users;"
```

### Errore: "Invalid JWT"

**Causa**: Il jwk-set-uri non è raggiungibile o il token è scaduto.

**Soluzione**:
- Verifica che Keycloak sia running
- Controlla che il realm sia corretto
- Rigenera un nuovo token

### Errore: "Table 'users' doesn't exist"

**Causa**: `schema.sql` non è stato eseguito.

**Soluzione**:
- Verifica che `spring.sql.init.mode=always` sia in `application.yml`
- Controlla i log di avvio per errori SQL

## 📝 Best Practices Implementate

✅ **Separazione dei livelli**: Controller → Service → Repository  
✅ **DTO pattern**: Non esponiamo mai le entity direttamente  
✅ **Transazioni**: Metodi service con `@Transactional`  
✅ **Idempotenza**: `getOrCreate()` è thread-safe  
✅ **Logging strutturato**: SLF4J con Lombok  
✅ **Validazione**: JWT claims validation nel controller  
✅ **Security**: Stateless sessions, CSRF disabled per API REST  
✅ **Testing**: Unit tests + Integration tests con Testcontainers  

## 🔄 Prossimi Passi

- [ ] Aggiungere endpoint PATCH `/api/v1/users/me` per update profilo
- [ ] Implementare upload avatar
- [ ] Aggiungere cache con Redis
- [ ] Implementare Flyway per database migrations
- [ ] Aggiungere OpenAPI/Swagger documentation
- [ ] Implementare rate limiting
- [ ] Aggiungere metrics con Micrometer

## 📄 License

MIT
