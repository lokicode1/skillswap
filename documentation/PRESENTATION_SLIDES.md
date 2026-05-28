# SkillSwap Presentation Slides

## Slide 1 - Title
- SkillSwap: Time-Banking & Barter Network
- Full-Stack Monolithic Web Application
- Presenter: [Your Name]
Speaker notes:
- Introduce the concept of skill exchange using time as currency.

## Slide 2 - Problem Statement
- Skilled people often need help in other domains
- Monetary payment is a barrier for many communities
- No simple platform for trustworthy local skill exchange
Speaker notes:
- Explain how time-banking creates reciprocal value without cash.

## Slide 3 - Solution Overview
- SkillSwap enables users to offer and request skills
- Book sessions through an integrated calendar
- Pay with Time Tokens (minutes)
Speaker notes:
- Emphasize transparency, accessibility, and reciprocity.

## Slide 4 - Tech Stack
- Backend: Spring Boot, Spring Security, Spring Data JPA
- Frontend: React, TypeScript, Vite, Tailwind CSS
- Database: PostgreSQL
- Build/Deploy: Maven, Docker, Render
Speaker notes:
- Mention monolith architecture for easier deployment and maintenance.

## Slide 5 - Architecture
- Single Maven project
- `frontend/` bundled into Spring Boot static assets
- One executable JAR for end-to-end application delivery
Speaker notes:
- Highlight reduced operational complexity and simpler CI/CD.

## Slide 6 - Core Features
- JWT authentication and protected APIs
- Skill Discovery Board with filtering
- Offer/Need posting workflows
- Booking lifecycle: request, confirm, complete, cancel
- Wallet ledger of Time Token transfers
Speaker notes:
- Demonstrate complete user flow from signup to completed exchange.

## Slide 7 - Database & Transactions
- Entities: User, SkillOffer, SkillNeed, BookingCalendar, TimeTransaction
- Token transfers handled in `@Transactional` service
- Row locking to prevent concurrent double-spend
Speaker notes:
- Stress data integrity and consistency guarantees.

## Slide 8 - UI & UX
- Responsive layout for desktop/tablet/mobile
- Dashboard with token balance and booking snapshot
- Clean, modern Tailwind styling
Speaker notes:
- Mention usability and professional design considerations.

## Slide 9 - Deployment
- Dockerized Spring Boot monolith
- Render blueprint (`render.yaml`) for web + PostgreSQL
- Environment-variable based DB credentials
Speaker notes:
- Explain production-safe secret management and managed database usage.

## Slide 10 - Demo Flow
- Register two users
- Post an offer
- Create and complete a booking
- Observe wallet balance transfer and transaction log
Speaker notes:
- This validates the business logic end-to-end.

## Slide 11 - Future Enhancements
- Real-time notifications and chat
- Ratings/reputation engine
- Admin moderation and reporting
- Recurring booking slots
Speaker notes:
- Show extensibility and roadmap potential.

## Slide 12 - Closing
- SkillSwap enables practical, community-first skill exchange
- Built as a production-ready full-stack monolith
- Thank you / Q&A
Speaker notes:
- Invite questions on architecture, deployment, and scaling.

