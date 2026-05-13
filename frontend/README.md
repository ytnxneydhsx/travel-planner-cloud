# Route Lantern Frontend

Spacious travel-planning frontend for the `travel-planner-cloud` gateway.

## Run

Start the test/debug backend stack first:

```powershell
cd ..\infra\nacos
.\scripts\prepare-mysql-schema.ps1
docker compose -f docker-compose.yml -f docker-compose.test.yml --profile microservices up -d --build
```

Then run the frontend:

```powershell
cd ..\..\frontend
npm install
$env:VITE_API_BASE_URL="http://localhost:18081"
npm run dev -- --port 5173
```

Open:

```text
http://localhost:5173
```

The frontend points to:

```text
VITE_API_BASE_URL=http://localhost:18081
```

## Docker

The Docker Compose stack builds the frontend with Node and serves the built app through Vite preview.

```powershell
cd ..\infra
docker compose --profile microservices up -d --build frontend
```

Open:

```text
http://localhost:5173
```

That matches the Dockerized `gateway-service` on `http://localhost:18081`.

## Seed Test Data

After the backend services have started and Flyway has created tables, seed demo data through the gateway:

```powershell
cd ..\infra\nacos
.\scripts\seed-test-data.ps1
```

Demo login:

```text
username: traveler_test
password: travel123
```
