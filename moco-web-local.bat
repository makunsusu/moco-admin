@echo off
setlocal enabledelayedexpansion
set "BASE_DIR=%~dp0"
set "UI_DIR=%BASE_DIR%moco-ui"
set "LOG_DIR=%BASE_DIR%logs"
set "PID_DIR=%BASE_DIR%runtime"
set "WEB_APP_NAME=moco-ui"
set "WEB_PORT=18081"
set "WEB_PID_FILE=%PID_DIR%\%WEB_APP_NAME%.pid"
set "WEB_LOG_FILE=%LOG_DIR%\%WEB_APP_NAME%.log"

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
if not exist "%PID_DIR%" mkdir "%PID_DIR%"
if not exist "%UI_DIR%\node_modules" (
  echo UI dependencies are missing: "%UI_DIR%\node_modules"
  echo Run: cd moco-ui ^&^& npm install
  exit /b 1
)

if /I "%~1"=="start" goto :start
if /I "%~1"=="stop" goto :stop
if /I "%~1"=="restart" goto :restart
if /I "%~1"=="status" goto :status
if /I "%~1"=="logs" goto :logs
goto :usage

:getpid
set "CURRENT_WEB_PID="
for /f "tokens=1" %%i in ('wmic process where "CommandLine like '%%moco-ui%%vue-cli-service serve%%' and name='node.exe'" get ProcessId ^| findstr /r "^[0-9]"') do (
  set "CURRENT_WEB_PID=%%i"
  goto :eof
)
if exist "%WEB_PID_FILE%" (
  set /p CURRENT_WEB_PID=<"%WEB_PID_FILE%"
  tasklist /fi "PID eq !CURRENT_WEB_PID!" | findstr /r " !CURRENT_WEB_PID! " >nul 2>nul
  if errorlevel 1 (
    set "CURRENT_WEB_PID="
    del /f /q "%WEB_PID_FILE%" >nul 2>nul
  )
)
goto :eof

:start
call :getpid
if defined CURRENT_WEB_PID (
  echo %WEB_APP_NAME% is already running, PID=!CURRENT_WEB_PID!
  exit /b 0
)
start "moco-ui" /b powershell -NoProfile -ExecutionPolicy Bypass -Command "Set-Location -LiteralPath '%UI_DIR%'; npm.cmd run dev -- --port %WEB_PORT% *> '%WEB_LOG_FILE%'"
timeout /t 8 /nobreak >nul
call :getpid
if defined CURRENT_WEB_PID (
  > "%WEB_PID_FILE%" echo !CURRENT_WEB_PID!
  echo %WEB_APP_NAME% started, PID=!CURRENT_WEB_PID!
  echo URL: http://localhost:%WEB_PORT%
  echo Log: %WEB_LOG_FILE%
  exit /b 0
)
echo %WEB_APP_NAME% failed to start, check log: %WEB_LOG_FILE%
exit /b 1

:stop
call :getpid
if not defined CURRENT_WEB_PID (
  echo %WEB_APP_NAME% is not running
  if exist "%WEB_PID_FILE%" del /f /q "%WEB_PID_FILE%" >nul 2>nul
  exit /b 0
)
taskkill /f /pid !CURRENT_WEB_PID! >nul 2>nul
if exist "%WEB_PID_FILE%" del /f /q "%WEB_PID_FILE%" >nul 2>nul
echo %WEB_APP_NAME% stopped
exit /b 0

:restart
call :stop
call :start
exit /b %errorlevel%

:status
call :getpid
if defined CURRENT_WEB_PID (
  echo %WEB_APP_NAME% is running, PID=!CURRENT_WEB_PID!, URL: http://localhost:%WEB_PORT%
  exit /b 0
)
echo %WEB_APP_NAME% is not running
exit /b 0

:logs
if not exist "%WEB_LOG_FILE%" type nul > "%WEB_LOG_FILE%"
type "%WEB_LOG_FILE%"
exit /b 0

:usage
echo Usage:
echo   moco-web-local.bat start
echo   moco-web-local.bat stop
echo   moco-web-local.bat restart
echo   moco-web-local.bat status
echo   moco-web-local.bat logs
exit /b 1
