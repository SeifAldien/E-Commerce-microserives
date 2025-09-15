param(
  [switch]$Detach
)

$composeFile = Join-Path $PSScriptRoot "..\infra\docker-compose.yml"
if (!(Test-Path $composeFile)) { Write-Error "docker-compose.yml not found"; exit 1 }

$args = @("-f", $composeFile, "up")
if ($Detach) { $args += "-d" }

docker compose @args


