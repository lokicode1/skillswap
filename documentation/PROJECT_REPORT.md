# SkillSwap - Project Report

## 1) Introduction
SkillSwap is a full-stack Time-Banking and barter platform where users exchange skills using "Time Tokens" measured in minutes.  
The goal is to create a trusted local/community economy where users can offer expertise, request help, schedule sessions, and settle value through tokenized time.

## 2) System Architecture
SkillSwap is built as a monolithic Java application with an embedded frontend:

- Backend: Spring Boot 3 (REST API, Security, JPA/Hibernate, JWT auth)
- Frontend: React + TypeScript + Vite + Tailwind CSS (inside `frontend/`)
- Database: PostgreSQL
- Build: Maven with `frontend-maven-plugin` to produce one executable JAR

### Runtime flow
1. User authenticates via `/api/auth/register` or `/api/auth/login`.
2. Backend issues JWT access token.
3. Frontend sends JWT in `Authorization: Bearer <token>`.
4. User performs skill discovery, posting, bookings, and token operations.

## 3) Database Schema Design (ER Description)
Core entities:

- **User (`app_user`)**
  - `id`, `email`, `displayName`, `passwordHash`, `role`, `tokenMinutesBalance`, `createdAt`
- **SkillOffer (`skill_offer`)**
  - Offer owner (User), title, description, category, location, active flag, minutes-per-hour
- **SkillNeed (`skill_need`)**
  - Requester (User), title, description, category, location, active flag
- **BookingCalendar (`booking_calendar`)**
  - Offer, requester, provider, start/end timestamps, token minutes, status lifecycle
- **TimeTransaction (`time_transaction`)**
  - fromUser, toUser, booking reference, minutes, memo, createdAt

Relationships:
- One user can create many offers and needs.
- One booking references one offer, one requester, and one provider.
- One completed booking can generate one time transaction transfer.

### Concurrency protection
Token transfer on booking completion uses:
- `@Transactional` service methods
- Pessimistic row lock (`PESSIMISTIC_WRITE`) on payer and payee `User` rows

This prevents double-spend and race conditions under concurrent booking completion requests.

## 4) API Endpoints
### Public endpoints
- `GET /api/health`
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/offers?q=...`
- `GET /api/needs?q=...`

### Authenticated endpoints
- `GET /api/me`
- `POST /api/offers`
- `POST /api/needs`
- `GET /api/bookings`
- `POST /api/bookings`
- `POST /api/bookings/{id}/confirm`
- `POST /api/bookings/{id}/complete`
- `POST /api/bookings/{id}/cancel`
- `GET /api/wallet/transactions`

## 5) User Guide
### Local development
1. Start PostgreSQL:
   - `docker compose up -d`
2. Run backend + embedded frontend:
   - `.\mvnw.cmd clean package`
   - `java -jar target/skillswap-0.0.1-SNAPSHOT.jar`
3. Open app:
   - `http://localhost:8080`

### Typical usage flow
1. Register two users.
2. User A posts a skill offer.
3. User B discovers and books that offer.
4. User A confirms and completes booking.
5. Wallet balances and transaction history update automatically.

## 6) Deployment (Render)
Use `render.yaml` blueprint:
- Provision one web service from Dockerfile
- Provision one managed PostgreSQL database
- Inject:
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
  - `SKILLSWAP_JWT_SECRET`

## 7) Conclusion
SkillSwap provides a production-style, responsive, JWT-secured, token-transaction-safe full-stack monolith that is easy to run locally and deploy on Render with environment-driven configuration.

