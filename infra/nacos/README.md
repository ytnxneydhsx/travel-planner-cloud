# Local infrastructure

This directory provides the local Docker infrastructure required to run the project in development.

Current scope:

- one shared MySQL instance for business services
- one dedicated MySQL instance for Nacos
- one Nacos instance for service discovery

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

Nacos MySQL defaults:

```text
host: localhost
port: 33060
username: root
password: 123456
database: nacos_config
```

## Stop

```powershell
cd infra\nacos
docker compose down
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
- `mysql-init/business`: bootstrap SQL for business databases
- `mysql-init/nacos`: generated Nacos schema SQL
- `scripts/prepare-mysql-schema.ps1`: downloads and prepares the Nacos schema for the configured version

The Java services already include Nacos discovery dependencies and registration properties.
The next step is runtime verification:

- start Docker Desktop and bring up `infra/nacos`
- verify all four services appear in the Nacos console
