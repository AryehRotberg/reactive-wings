# ✈️ Reactive Wings – Flight Subscription Manager

Reactive, real-time flight monitNotes:
- There's no SMTP fallback; email is sent via SendGrid SDK.
- CORS is configured to allow the React frontend and local development origins: `https://reactivewings.vercel.app`, `http://35.224.131.227.nip.io:8080`, `http://localhost:8080`, `http://localhost:3000`, `http://localhost:5173` (see `SecurityConfig`).ng and email notifications with Spring Boot WebFlux. The app ingests Ben Gurion Airport flight data, lets users subscribe to flights, and sends HTML email alerts on changes.

## 🚀 Highlights

- Real-time flight sync from data.gov.il every 60s
- Subscriptions checked every 10s with change detection (schedule, terminal, counters, check-in zone, status)
- Google OAuth 2.0 login (resource server + login) – most endpoints require auth
- SendGrid-based HTML emails (confirmation + updates)
- RESTful API backend for separate React frontend
- Docker-ready; Java 21; Spring Boot 3.5.5 WebFlux

## 🌐 Live Application

Frontend (React): [https://reactivewings.vercel.app](https://reactivewings.vercel.app)  
Backend API: Deployed separately (configured via CORS)

## 🛠️ Tech Stack

- Spring Boot WebFlux 3.5.5
- Reactive MongoDB (Spring Data)
- Spring Security OAuth2 Client & Resource Server (Google)
- springdoc-openapi for Swagger UI
- SendGrid Java SDK
- Maven, Docker, Java 21

## 📦 Project Structure (abridged)

```
src/main/java/com/example/flights/
├─ FlightsApplication.java           # Main app (+ scheduling)
├─ config/SecurityConfig.java        # OAuth2 login, JWT, CORS
├─ controller/                       # REST endpoints
│  ├─ FlightsController.java         # /flights, /flights/search
│  └─ UserController.java            # /users/* (auth required)
├─ model/                            # Flight/User/Subscription
├─ repo/                             # Reactive repositories
├─ service/                          # Sync, subscription, email, API client
└─ template/EmailTemplates.java      # Email HTML templates

src/main/resources/
└─ application.properties            # Uses env var placeholders
```

> **Note:** The frontend is a separate React application hosted at [https://reactivewings.vercel.app](https://reactivewings.vercel.app)

## 🔌 API

Authentication: All routes require Google OAuth login. The frontend React application at [https://reactivewings.vercel.app](https://reactivewings.vercel.app) handles user authentication and communicates with this backend API.

- GET `/flights`
  - Query: `page` (default 0), `size` (default 100, max 500)
  - Sorted by `lastUpdated` desc

- GET `/flights/search`
  - Query filters (all optional):
    - `airline_code` (e.g., EL AL)
    - `flight_number` (e.g., LY001)
    - `scheduled_date` (prefix match, e.g., 2025-09-07)
    - `estimated_date` (prefix match)
    - `direction` (ARRIVAL/DEPARTURE)
    - `city` (matches `city_name`)
    - `status` (matches `status_en`)
  - Also supports `page` and `size` like `/flights`

- GET `/users/user-info`  (auth)
- POST `/users/subscribe` (auth, body: SubscriptionModel)
- POST `/users/unsubscribe` (auth, query: `airline_code`, `flight_number`, `scheduled_date`)

OpenAPI docs (requires login):
- Swagger UI: `/swagger-ui.html` (redirects to `/swagger-ui/index.html`)
- OpenAPI JSON: `/v3/api-docs`

## ⚙️ Configuration

The app reads config from environment variables (see `application.properties`). Required for a functional run:

- `MONGODB_URI` – Mongo connection string
- `MONGODB_DATABASE` – Database name
- `GOOGLE_CLIENT_ID` – Google OAuth client ID
- `GOOGLE_CLIENT_SECRET` – Google OAuth client secret
- `SENDGRID_API_KEY` – SendGrid API key
- Optional: `SECURITY_JWT_AUDIENCE` – if you need audience validation (maps to `security.jwt.audience`)

Notes:
- There’s no SMTP fallback; email is sent via SendGrid SDK.
- CORS allows these origins by default: `https://reactivewings.vercel.app`, `http://35.224.131.227.nip.io:8080`, `http://localhost:8080`, `http://localhost:3000`, `http://localhost:5173` (see `SecurityConfig`).

## 🏃 Run Locally

Prereqs: Java 21+, Maven, MongoDB Atlas or compatible instance, Google OAuth app, SendGrid account.

1) Set env vars (example)

```bash
# Linux/macOS/Git Bash
export MONGODB_URI="mongodb+srv://..."
export MONGODB_DATABASE="flights"
export GOOGLE_CLIENT_ID="..."
export GOOGLE_CLIENT_SECRET="..."
export SENDGRID_API_KEY="..."
```

2) Start the app

```bash
# Unix shells
./mvnw spring-boot:run

# Windows (PowerShell/CMD)
mvnw.cmd spring-boot:run
```

API will be available at: http://localhost:8080  
Frontend: Use the React app at [https://reactivewings.vercel.app](https://reactivewings.vercel.app) or run it locally

## 🐳 Docker

Build the JAR and image, then run with required env vars.

```bash
./mvnw clean package
docker build -t reactivewings:local .
docker run --rm -p 8080:8080 \
  -e MONGODB_URI="..." \
  -e MONGODB_DATABASE="flights" \
  -e GOOGLE_CLIENT_ID="..." \
  -e GOOGLE_CLIENT_SECRET="..." \
  -e SENDGRID_API_KEY="..." \
  reactivewings:local
```

There’s also a minimal `compose.yaml`. To use Compose, add an `environment:` block or pass variables at runtime.

Example `.env` file (not committed):

```
MONGODB_URI=...
MONGODB_DATABASE=flights
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...
SENDGRID_API_KEY=...
```

Run:

```bash
docker compose up --build
```

## ⏱️ Schedulers

- Flight sync: every 60s (`FlightSyncService`)
- Subscription checks: every 10s (`SubscriptionService`)

## 📨 Email

`EmailSenderService` uses SendGrid SDK and the HTML templates in `template/EmailTemplates.java` to send:
- Subscription confirmation
- Flight change updates (with change log)

Ensure the sender is verified in SendGrid. Update the hard-coded "from" address in `EmailSenderService` if needed.

## 🧪 Dev & Testing

```bash
# Run unit tests
./mvnw test

# Build without tests
./mvnw clean package -DskipTests
```

Swagger UI is behind OAuth login; authenticate via the frontend application to explore the API at `/swagger-ui.html`.

## 🧰 Scripts

- `scripts/build-and-push-docker.sh` builds the JAR and pushes to Docker Hub image `aryehrotberg709/reactivewings:<version>`.
- `scripts/run-application.sh` exports env vars and runs the app.

Security note: Do NOT commit real secrets. Rotate any exposed keys immediately and store values in your local environment or a secret manager. Consider deleting or sanitizing `scripts/run-application.sh` and adding a `.env.example` instead.

## 📚 Data Source

Ben Gurion International Airport (data.gov.il)
- Endpoint: `https://data.gov.il/api/3/action/datastore_search`
- Resource ID: `e83f763b-b7d7-479e-b172-ae981ddc6de5`

## 🔒 Security

- OAuth2 with Google; tokens validated via issuer `https://accounts.google.com`
- JWT audience validation is optional (configure `SECURITY_JWT_AUDIENCE`)
- CORS restrictions as configured in `SecurityConfig`

## 📄 License

No license file is present in this repository. If you intend the code to be open source, add a LICENSE file (e.g., MIT, Apache-2.0).

## 🤝 Contributing

Issues and PRs are welcome. Please include tests for behavior changes and update this README when APIs/config change.

---

Built with ❤️ using Spring Boot WebFlux
