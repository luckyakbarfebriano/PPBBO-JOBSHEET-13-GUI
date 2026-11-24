@echo off
cd /d "%~dp0"
if not exist bin mkdir bin
javac -cp "lib\postgresql-42.7.4.jar" -d bin src\Stand.java src\DatabaseHelper.java src\StandApp.java
if errorlevel 1 (
    pause
    exit /b 1
)
java -cp "bin;lib\postgresql-42.7.4.jar" StandApp
