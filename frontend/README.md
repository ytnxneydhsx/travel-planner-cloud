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
npm run dev
```

Open:

```text
http://localhost:3000
```

The frontend points to:

```text
VITE_API_BASE_URL=http://localhost:8080
```

That matches `gateway-service` in the Docker test/debug stack.

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
