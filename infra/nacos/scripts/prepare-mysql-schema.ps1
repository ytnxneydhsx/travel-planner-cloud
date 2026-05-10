$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $root ".env"
$targetDir = Join-Path $root "mysql-init"
$targetFile = Join-Path $targetDir "mysql-schema.sql"

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
    Invoke-WebRequest -Uri $newSchemaUrl -OutFile $targetFile -UseBasicParsing
} catch {
    Invoke-WebRequest -Uri $legacySchemaUrl -OutFile $targetFile -UseBasicParsing
}

if (-not (Test-Path $targetFile) -or (Get-Item $targetFile).Length -eq 0) {
    throw "Failed to prepare Nacos MySQL schema: $targetFile"
}

Write-Host "Prepared Nacos MySQL schema: $targetFile"
