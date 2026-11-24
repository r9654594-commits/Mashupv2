@echo off
setlocal
set "GRADLE_USER_HOME=%~dp0.gradle-user"
call "%~dp0gradlew.bat" runClient --no-daemon
if errorlevel 1 pause
