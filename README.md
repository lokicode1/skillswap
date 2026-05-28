# SkillSwap

SkillSwap is a full-stack Time-Banking and Barter Network where users trade skills using Time Tokens (minutes).

## Tech Stack
- Backend: Spring Boot 3, Spring Security, JPA/Hibernate, JWT
- Frontend: React + TypeScript + Vite + Tailwind CSS
- DB: PostgreSQL
- Build: Maven monolith (frontend embedded into Spring Boot JAR)

## Run Locally
1. Start PostgreSQL:
   - `docker compose up -d`
2. Build:
   - `.\mvnw.cmd clean package`
3. Start app:
   - `java -jar target/skillswap-0.0.1-SNAPSHOT.jar`
4. Open:
   - `http://localhost:8080`

## Key APIs
- Public:
  - `GET /api/health`
  - `POST /api/auth/register`
  - `POST /api/auth/login`
  - `GET /api/offers`
  - `GET /api/needs`
- Auth required:
  - `GET /api/me`
  - `POST /api/offers`
  - `POST /api/needs`
  - `GET/POST /api/bookings`
  - `POST /api/bookings/{id}/confirm`
  - `POST /api/bookings/{id}/complete`
  - `POST /api/bookings/{id}/cancel`
  - `GET /api/wallet/transactions`

## Deploy on Render (Blueprint)
This repo includes `render.yaml`.

1. In Render: **New > Blueprint**
2. Select this GitHub repo
3. Apply blueprint

Render provisions:
- Web service: `skillswap-app` (Docker runtime)
- Managed Postgres: `skillswap-db`

The web service uses:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SKILLSWAP_JWT_SECRET`

