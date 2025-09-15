$composeFile = Join-Path $PSScriptRoot "..\infra\docker-compose.yml"
if (!(Test-Path $composeFile)) { Write-Error "docker-compose.yml not found"; exit 1 }

docker compose -f $composeFile down -v


