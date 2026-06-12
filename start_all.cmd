@echo off
setlocal

set "PROJECT=%~dp0"

echo == Raeume alte Projekt-Prozesse auf ==
call "%PROJECT%stop_all.cmd"

echo == Pruefe Docker Infrastruktur ==
pushd "%PROJECT%docker"
call :ensure_compose_service database
if errorlevel 1 (
    echo Docker database konnte nicht gestartet werden.
    popd
    pause
    exit /b 1
)

echo Starte database neu, um alte DB-Verbindungen zu schliessen ...
docker compose restart database
if errorlevel 1 (
    echo Docker database konnte nicht neu gestartet werden.
    popd
    pause
    exit /b 1
)
echo Warte 5 Sekunden auf PostgreSQL...
timeout /t 5 /nobreak > nul

call :ensure_compose_service rabbitmq
if errorlevel 1 (
    echo Docker rabbitmq konnte nicht gestartet werden.
    pause
    popd
    exit /b 1
)
docker compose ps
popd

echo == Starte API in neuem Terminal ==
start "energyapi" /D "%PROJECT%energyapi" cmd /k mvn clean spring-boot:run

echo Warte 15 Sekunden auf API-Start...
timeout /t 15 /nobreak > nul

echo == Starte Usage Service in neuem Terminal ==
start "usage-service" /D "%PROJECT%usage-service" cmd /k mvn clean spring-boot:run

echo == Starte Current Percentage Service in neuem Terminal ==
start "current-percentage-service" /D "%PROJECT%current-percentage-service" cmd /k mvn clean spring-boot:run

echo Warte 10 Sekunden auf Consumer-Services...
timeout /t 10 /nobreak > nul

echo == Starte Producer in neuem Terminal ==
start "energy-producer" /D "%PROJECT%energy-producer" cmd /k mvn clean spring-boot:run

echo == Starte User in neuem Terminal ==
start "energy-user" /D "%PROJECT%energy-user" cmd /k mvn clean spring-boot:run

echo Warte 10 Sekunden auf erste Messages...
timeout /t 10 /nobreak > nul

echo == Starte GUI in neuem Terminal ==
start "energy-gui" /D "%PROJECT%GUI" cmd /k mvn clean javafx:run

echo.
echo Live-Umgebung wurde gestartet.
echo In der GUI kannst du refresh und show data testen.
echo Dieses Fenster kann geschlossen werden.

goto :finish

:ensure_compose_service
set "SERVICE=%~1"
set "CONTAINER_ID="
set "IS_RUNNING="

for /f "delims=" %%i in ('docker compose ps -q %SERVICE%') do set "CONTAINER_ID=%%i"

if defined CONTAINER_ID (
    for /f "delims=" %%s in ('docker inspect -f "{{.State.Running}}" %CONTAINER_ID% 2^>nul') do set "IS_RUNNING=%%s"
    if /I "%IS_RUNNING%"=="true" (
        echo %SERVICE% laeuft bereits.
        exit /b 0
    )
)

echo Starte %SERVICE% ...
docker compose up -d %SERVICE%
exit /b %errorlevel%

:finish
endlocal
