# mysql-init

This directory contains initialization files for two separate MySQL containers.

Current responsibilities:

- `business/01-create-business-databases.sql`
  Creates the business databases used by:
  - `user-service`
  - `destination-service`
  - `itinerary-service`
- `nacos/mysql-schema.sql`
  Generated from the Nacos version in `.env` and executed by the dedicated Nacos MySQL instance

Before first startup, generate the Nacos schema:

```powershell
.\scripts\prepare-mysql-schema.ps1
```

The generated SQL file is ignored by Git because it is derived from the Nacos version in `.env`.
