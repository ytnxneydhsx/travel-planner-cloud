# Nacos infrastructure

This directory provides the local Docker infrastructure for Nacos service discovery.

## Ports

- Nacos console: `http://localhost:18080`
- Nacos server endpoint for Spring Cloud: `localhost:8848`
- Nacos gRPC port: `localhost:9848`
- Nacos MySQL host port: `localhost:33060`

The console uses `18080` to avoid conflicting with `gateway-service` on `8080`.
The MySQL host port uses `33060` to avoid conflicting with local business databases on `3306`.

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

## Current scope

This infrastructure is ready for Nacos-based service discovery.
The Java services already include Nacos discovery dependencies and registration properties.
The next step is runtime verification:

- start Docker Desktop and bring up `infra/nacos`
- start `gateway-service`
- start `user-service`
- start `destination-service`
- start `itinerary-service`
- verify all four services appear in the Nacos console
