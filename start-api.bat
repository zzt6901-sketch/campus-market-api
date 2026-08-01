@echo off
cd /d "%~dp0"
if not "%~1"=="" set "DB_PASSWORD=%~1"
set "JWT_SECRET=local-one-click-start-secret-change-me"
echo  [api] Backend started. Press Ctrl+C to stop.
java -jar target\campus-trading-1.0.0.jar
