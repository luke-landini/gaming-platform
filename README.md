# Gaming Platform

Questa è una piattaforma di gaming basata su una architettura a microservizi. Il progetto include servizi per la gestione del profilo utente, il catalogo dei giochi e le classifiche (leaderboard), con un frontend moderno in React.

## 🚀 Tecnologie Utilizzate

### Frontend
- **React 19**: Libreria core per l'interfaccia utente.
- **Vite**: Strumento di build e server di sviluppo veloce.
- **Keycloak JS**: Integrazione per l'autenticazione e autorizzazione.
- **Axios**: Client HTTP per le chiamate API.

### Backend (Microservizi)
- **Spring Boot 3.2.2**: Framework principale per tutti i servizi.
- **Spring Security & OAuth2 Resource Server**: Gestione della sicurezza e validazione dei token JWT emessi da Keycloak.
- **Spring Data JPA**: Astrazione per l'accesso ai dati.
- **PostgreSQL**: Database relazionale utilizzato da tutti i servizi.
- **Redis**: Utilizzato dal `leaderboard-service` per caching e alte prestazioni nelle classifiche.
- **Apache Kafka**: Sistema di messaggistica utilizzato per la comunicazione asincrona tra i servizi (es. aggiornamenti punteggi).
- **Lombok**: Libreria per ridurre il codice boilerplate Java.
- **MapStruct**: Utilizzato nel `game-catalog-service` per il mapping dei DTO.

### Infrastruttura e DevOps
- **Docker & Docker Compose**: Orchestrazione dei servizi infrastrutturali (DB, Keycloak, Kafka, Redis).
- **Keycloak**: Identity and Access Management (IAM) per la gestione di utenti e permessi.
- **Testcontainers**: Utilizzato nei test di integrazione per avviare istanze reali di DB e Kafka.
- **pgAdmin**: Interfaccia web per la gestione dei database PostgreSQL.

---

## 📂 Struttura del Progetto

- `frontend/`: Applicazione React.
- `user-profile-service/`: Gestione utenti e profili.
- `game-catalog-service/`: Catalogo dei giochi disponibili.
- `leaderboard-service/`: Gestione delle classifiche globali.
- `infra/`: Configurazioni Docker Compose, script SQL di inizializzazione e temi Keycloak.

---

## 🛠️ Setup del Progetto

### Prerequisiti
- Java 17 o superiore
- Node.js (versione LTS consigliata)
- Docker e Docker Compose
- Maven

### 1. Avvio dell'Infrastruttura
Tutta l'infrastruttura necessaria (DB, Keycloak, Redis, Kafka) può essere avviata tramite Docker:

```bash
cd infra
docker-compose up -d
```

### 2. Configurazione Keycloak
Keycloak sarà disponibile all'indirizzo `http://localhost:8080`.
- Accedi alla console di amministrazione (`admin`/`admin`).
- Il progetto è pre-configurato per utilizzare un realm chiamato `myrealm`.
- È presente un tema personalizzato (`gaming-dark`) in `infra/keycloak-theme`.

### 3. Avvio dei Microservizi
Per ogni servizio (`user-profile-service`, `game-catalog-service`, `leaderboard-service`), esegui:

```bash
mvn clean install
mvn spring-boot:run
```

### 4. Avvio del Frontend
Entra nella cartella frontend e avvia il server di sviluppo:

```bash
cd frontend
npm install
npm run dev
```

L'applicazione sarà accessibile all'indirizzo `http://localhost:5173`.

---

## 🧪 Testing
I test possono essere eseguiti singolarmente per ogni microservizio:

```bash
mvn test
```
I servizi utilizzano **Testcontainers**, quindi assicurati che Docker sia in esecuzione durante i test.
