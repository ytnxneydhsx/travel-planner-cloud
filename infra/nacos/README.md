# Local infrastructure

This directory provides the local Docker infrastructure required to run the project in development.

Current scope:

- one shared MySQL instance for business services
- one dedicated MySQL instance for Nacos
- one Nacos instance for service discovery
- optional Dockerized microservices profile for the full application stack

The directory name remains `infra/nacos` for compatibility, but its scope is now the full local runtime stack rather than Nacos alone.

## Ports

- Nacos console: `http://localhost:18080`
- Nacos server endpoint for Spring Cloud: `localhost:8848`
- Nacos gRPC port: `localhost:9848`
- Business MySQL host port: `localhost:3306`
- Nacos MySQL host port: `localhost:33060`

The console uses `18080` to avoid conflicting with `gateway-service` on `8080`.
The business MySQL uses `3306` because the business services already default to `localhost:3306`.
The Nacos MySQL uses `33060` to keep infrastructure data separate from business data.

## First startup

Generate the Nacos MySQL schema before the first MySQL startup:

```powershell
cd infra\nacos
.\scripts\prepare-mysql-schema.ps1
docker compose up -d
```

After startup, open:

```text
http://localhost:18080
```

Default console credentials:

```text
username: nacos
password: nacos
```

Shared MySQL defaults:

```text
host: localhost
port: 3306
username: root
password: 123456
```

Business databases created on first startup:

- `travel_user`
- `travel_destination`
- `travel_itinerary`
- `travel_user_test`
- `travel_destination_test`
- `travel_itinerary_test`

Nacos MySQL defaults:

```text
host: localhost
port: 33060
username: root
password: 123456
database: nacos_config
```

## Run modes

Infrastructure only:

```powershell
cd infra\nacos
.\scripts\prepare-mysql-schema.ps1
docker compose up -d
```

Full microservice stack in Docker:

```powershell
cd infra\nacos
.\scripts\prepare-mysql-schema.ps1
docker compose --profile microservices up -d --build
```

Test/debug microservice stack in Docker:

```powershell
cd infra\nacos
.\scripts\prepare-mysql-schema.ps1
docker compose -f docker-compose.yml -f docker-compose.test.yml --profile microservices up -d --build
```

The test/debug stack keeps the same container names and published ports as the default stack, but the services load `env/*.test.env`.
Use it instead of the default stack, not at the same time.

Test/debug environment differences:

- `SPRING_PROFILES_ACTIVE=test`
- business services use `travel_user_test`, `travel_destination_test`, and `travel_itinerary_test`
- `JWT_SECRET` is shared by `user-service` and `gateway-service` for test tokens
- `JWT_EXPIRE_SECONDS=3600`
- `LOGGING_LEVEL_ORG_EXAMPLE=DEBUG`

If `business-mysql` was already initialized before the test database script was added, create the test databases manually or reset local data.
Manual SQL:

```sql
CREATE DATABASE IF NOT EXISTS travel_user_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS travel_destination_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS travel_itinerary_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

After the test/debug services have started and Flyway has created the tables, seed demo data through the gateway:

```powershell
.\scripts\seed-test-data.ps1
```

Demo login:

```text
username: traveler_test
password: travel123
```

The `microservices` profile starts:

- `user-service`
- `destination-service`
- `itinerary-service`
- `gateway-service`

Published service ports:

- gateway: `http://localhost:8080`
- destination-service: `http://localhost:8081`
- user-service: `http://localhost:8082`
- itinerary-service: `http://localhost:8083`

## Stop

```powershell
cd infra\nacos
docker compose down
```

Stop the full stack including the Dockerized services:

```powershell
cd infra\nacos
docker compose --profile microservices down
```

## Reset local data

Stop containers first, then remove `data/` and `logs/`.

```powershell
cd infra\nacos
docker compose down
Remove-Item -Recurse -Force .\data, .\logs
```

## Directory layout

- `env/business-mysql.env`: business MySQL container environment
- `env/nacos-mysql.env`: Nacos MySQL container environment
- `env/nacos.env`: Nacos server environment
- `env/*-service.env`: Dockerized Spring Boot service environment
- `mysql-init/business`: bootstrap SQL for business databases
- `mysql-init/nacos`: generated Nacos schema SQL
- `scripts/prepare-mysql-schema.ps1`: downloads and prepares the Nacos schema for the configured version

The Java services already include Nacos discovery dependencies and registration properties.
For the full Dockerized stack, the next step is runtime verification:

- start Docker Desktop and bring up `infra/nacos` with the `microservices` profile
- verify all four services appear in the Nacos console
- verify requests through `gateway-service` on `http://localhost:8080`
