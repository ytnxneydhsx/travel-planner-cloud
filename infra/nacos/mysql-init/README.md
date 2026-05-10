# mysql-init

This directory is mounted into the MySQL container as `/docker-entrypoint-initdb.d`.

Before first startup, generate `mysql-schema.sql`:

```powershell
.\scripts\prepare-mysql-schema.ps1
```

The generated SQL file is ignored by Git because it is derived from the Nacos version in `.env`.
