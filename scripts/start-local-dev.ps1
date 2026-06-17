$ErrorActionPreference = "Stop"

$root = Resolve-Path -LiteralPath (Join-Path $PSScriptRoot "..")
$apiDir = Join-Path $root "backend\sebodigital-api"
$localEnv = Join-Path $PSScriptRoot "local-env.ps1"
$staticHost = if ($env:STATIC_HOST) { $env:STATIC_HOST } else { "127.0.0.1" }
$staticPort = if ($env:STATIC_PORT) { [int]$env:STATIC_PORT } else { 5500 }

function Test-LocalPort {
    param(
        [string]$HostName,
        [int]$Port
    )

    try {
        $client = [System.Net.Sockets.TcpClient]::new()
        $connection = $client.BeginConnect($HostName, $Port, $null, $null)
        if (-not $connection.AsyncWaitHandle.WaitOne(300)) {
            $client.Close()
            return $false
        }

        $client.EndConnect($connection)
        $client.Close()
        return $true
    } catch {
        return $false
    }
}

if (Test-Path -LiteralPath $localEnv) {
    . $localEnv
    Write-Host "Credenciais locais carregadas de scripts\local-env.ps1"
} else {
    Write-Warning "Arquivo scripts\local-env.ps1 nao encontrado. Copie scripts\local-env.example.ps1 e preencha suas credenciais."
}

if (Test-LocalPort -HostName $staticHost -Port $staticPort) {
    Write-Host "Frontend estatico ja esta rodando em http://${staticHost}:${staticPort}/"
} else {
    $logDir = Join-Path $root "logs"
    New-Item -ItemType Directory -Force -Path $logDir | Out-Null
    $out = Join-Path $logDir "static-server.log"
    $err = Join-Path $logDir "static-server.err"
    Start-Process -FilePath "node" `
        -ArgumentList @("scripts/static-server.mjs") `
        -WorkingDirectory $root `
        -WindowStyle Hidden `
        -RedirectStandardOutput $out `
        -RedirectStandardError $err | Out-Null
    Write-Host "Frontend estatico iniciado em http://${staticHost}:${staticPort}/"
}

Write-Host "Abra: http://${staticHost}:${staticPort}/index.html"
Write-Host "API Spring Boot: http://localhost:8080"
Write-Host ""

Push-Location -LiteralPath $apiDir
try {
    .\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
} finally {
    Pop-Location
}
