$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $root ".env"
$targetDir = Join-Path $root "mysql-init\\nacos"
$targetFile = Join-Path $targetDir "mysql-schema.sql"
$temporaryFile = Join-Path $targetDir "mysql-schema.raw.sql"

if (-not (Test-Path $envFile)) {
    throw "Missing .env file: $envFile"
}

$nacosVersionLine = Get-Content $envFile | Where-Object { $_ -match "^NACOS_VERSION=" } | Select-Object -First 1
if (-not $nacosVersionLine) {
    throw "NACOS_VERSION is not configured in $envFile"
}

$nacosVersion = ($nacosVersionLine -split "=", 2)[1].Trim()
$cleanVersion = $nacosVersion.TrimStart("v")
$cleanVersion = $cleanVersion -replace "-.*$", ""

$newSchemaUrl = "https://raw.githubusercontent.com/alibaba/nacos/$cleanVersion/plugin-default-impl/nacos-default-datasource-plugin/nacos-datasource-plugin-mysql/src/main/resources/META-INF/mysql-schema.sql"
$legacySchemaUrl = "https://raw.githubusercontent.com/alibaba/nacos/$cleanVersion/distribution/conf/mysql-schema.sql"

New-Item -ItemType Directory -Force -Path $targetDir | Out-Null

try {
    Invoke-WebRequest -Uri $newSchemaUrl -OutFile $temporaryFile -UseBasicParsing
} catch {
    Invoke-WebRequest -Uri $legacySchemaUrl -OutFile $temporaryFile -UseBasicParsing
}

if (-not (Test-Path $temporaryFile) -or (Get-Item $temporaryFile).Length -eq 0) {
    throw "Failed to download Nacos MySQL schema: $temporaryFile"
}

@"
CREATE DATABASE IF NOT EXISTS nacos_config
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE nacos_config;

"@ | Set-Content -Path $targetFile -Encoding UTF8
Get-Content -Path $temporaryFile -Raw -Encoding UTF8 | Add-Content -Path $targetFile -Encoding UTF8
@'

INSERT IGNORE INTO users (username, password, enabled)
VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE);

INSERT IGNORE INTO roles (username, role)
VALUES ('nacos', 'ROLE_ADMIN');
'@ | Add-Content -Path $targetFile -Encoding UTF8
Remove-Item $temporaryFile -Force

Write-Host "Prepared Nacos MySQL schema: $targetFile"
