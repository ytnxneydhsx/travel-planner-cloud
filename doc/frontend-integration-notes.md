# Frontend Integration Notes

## Why This Was Added

The backend already exposes the required microservice flow through `gateway-service`, so the frontend does not need to call individual services directly.

The frontend was added as a separate React/Vite app to keep UI iteration independent from the Java microservices while still using the real gateway contract:

- authentication through `/users/register` and `/users/login`
- destination discovery through `/destinations`
- itinerary creation and management through `/itineraries`

## Backend Code Changes

No Java backend business code was changed for the frontend.

The only backend-adjacent additions are environment and testing support:

- `infra/docker-compose.test.yml`
- `infra/nacos/env/*.test.env`
- `infra/nacos/mysql-init/business/02-create-test-business-databases.sql`
- `infra/nacos/scripts/seed-test-data.ps1`

These files exist so the frontend can run against isolated test databases and seeded demo data without polluting the default development databases.

## Frontend Changes

The frontend lives in `frontend/`.

Important files:

- `frontend/src/api.js`: gateway API client and token storage
- `frontend/src/main.jsx`: React app shell, auth flow, destination search, itinerary workflow
- `frontend/src/styles.css`: Spacious Product UI styling and responsive layout
- `frontend/.env.test`: points frontend requests at `http://localhost:8080`

## Design Direction

The UI uses a Spacious Product UI direction with light travel-editorial accents:

- generous whitespace and large rounded panels
- destination image cards
- itinerary timeline
- compact route summary panel
- warm sand background, sky blue accents, pine green actions, terracotta highlights

This matches the travel-planning use case better than a dense admin dashboard.

## How To Run

Start backend test/debug stack:

```powershell
cd infra
.\nacos\scripts\prepare-mysql-schema.ps1
docker compose -f docker-compose.yml -f docker-compose.test.yml --profile microservices up -d --build
```

Seed demo data:

```powershell
.\nacos\scripts\seed-test-data.ps1
```

Start frontend:

```powershell
cd ..\frontend
npm install
npm run dev
```

Open:

```text
http://localhost:3000
```

Demo account:

```text
username: traveler_test
password: travel123
```
