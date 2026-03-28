@echo off
setlocal enabledelayedexpansion

set "APP_NAME=moco-admin"
set "BASE_DIR=%~dp0"
set "JAR_PATH=%BASE_DIR%moco-admin\target\%APP_NAME%.jar"
set "UI_DIR=%BASE_DIR%moco-ui"
set "UI_PACKAGE_JSON=%UI_DIR%\package.json"
set "LOG_DIR=%BASE_DIR%logs"
set "PID_DIR=%BASE_DIR%runtime"
set "PID_FILE=%PID_DIR%\%APP_NAME%.pid"
set "LOG_FILE=%LOG_DIR%\%APP_NAME%.log"
set "WEB_APP_NAME=moco-ui"
set "WEB_PID_FILE=%PID_DIR%\%WEB_APP_NAME%.pid"
set "WEB_LOG_FILE=%LOG_DIR%\%WEB_APP_NAME%.log"
set "COMPOSE_FILE=%BASE_DIR%docker-compose.yml"
set "DOCKER_COMPOSE_CMD="

if defined JAVA_HOME (
  set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
) else (
  set "JAVA_CMD=java"
)

if not defined JVM_OPTS (
  set "JVM_OPTS=-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError"
)
if not defined WEB_PORT set "WEB_PORT=18081"

if "%~1"=="" goto :usage
if /I "%~1"=="start" goto :start
if /I "%~1"=="stop" goto :stop
if /I "%~1"=="restart" goto :restart
if /I "%~1"=="status" goto :status
if /I "%~1"=="logs" goto :logs
if /I "%~1"=="web-start" goto :webstart
if /I "%~1"=="web-stop" goto :webstop
if /I "%~1"=="web-restart" goto :webrestart
if /I "%~1"=="web-status" goto :webstatus
if /I "%~1"=="web-logs" goto :weblogs
if /I "%~1"=="deps-up" goto :depsup
if /I "%~1"=="deps-down" goto :depsdown
if /I "%~1"=="deps-restart" goto :depsrestart
if /I "%~1"=="deps-status" goto :depsstatus
if /I "%~1"=="up" goto :up
if /I "%~1"=="down" goto :down
goto :usage

:prepare
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
if not exist "%PID_DIR%" mkdir "%PID_DIR%"
if not exist "%JAR_PATH%" (
  echo 未找到 "%JAR_PATH%"
  echo 请先在项目根目录执行: mvn clean package -DskipTests
  exit /b 1
)
where "%JAVA_CMD%" >nul 2>nul
if errorlevel 1 (
  echo 未找到 java，请先安装 JDK 17 并设置 JAVA_HOME。
  exit /b 1
)
exit /b 0

:prepare_ui
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
if not exist "%PID_DIR%" mkdir "%PID_DIR%"
if not exist "%UI_PACKAGE_JSON%" (
  echo 未找到 "%UI_PACKAGE_JSON%"
  exit /b 1
)
if not exist "%UI_DIR%\node_modules" (
  echo 未找到 "%UI_DIR%\node_modules"
  echo 请先执行: cd moco-ui ^&^& npm install
  exit /b 1
)
where npm.cmd >nul 2>nul
if errorlevel 1 (
  echo 未找到 npm，请先安装 Node.js 18+ 和 npm 9+。
  exit /b 1
)
exit /b 0

:prepare_compose
if not exist "%COMPOSE_FILE%" (
  echo 未找到 "%COMPOSE_FILE%"
  exit /b 1
)
docker compose version >nul 2>nul
if not errorlevel 1 (
  set "DOCKER_COMPOSE_CMD=docker compose"
  exit /b 0
)
where docker-compose >nul 2>nul
if not errorlevel 1 (
  set "DOCKER_COMPOSE_CMD=docker-compose"
  exit /b 0
)
echo 未找到 docker compose，请先安装 Docker Desktop 或 Docker Compose。
exit /b 1

:getpid
set "CURRENT_PID="
for /f "tokens=1" %%i in ('wmic process where "CommandLine like '%%moco-admin\\target\\moco-admin.jar%%' and name='java.exe'" get ProcessId ^| findstr /r "^[0-9]"') do (
  set "CURRENT_PID=%%i"
  goto :eof
)
if exist "%PID_FILE%" (
  set /p CURRENT_PID=<"%PID_FILE%"
)
goto :eof

:getwebpid
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
call :prepare || exit /b 1
call :getpid
if defined CURRENT_PID (
  echo %APP_NAME% 已在运行，PID=!CURRENT_PID!
  exit /b 0
)
start "moco-admin" /b "%JAVA_CMD%" %JVM_OPTS% -jar "%JAR_PATH%" %APP_ARGS% >> "%LOG_FILE%" 2>&1
timeout /t 2 /nobreak >nul
call :getpid
if defined CURRENT_PID (
  > "%PID_FILE%" echo !CURRENT_PID!
  echo %APP_NAME% 启动成功，PID=!CURRENT_PID!
  echo 日志文件: %LOG_FILE%
  exit /b 0
)
echo %APP_NAME% 启动失败，请检查日志: %LOG_FILE%
exit /b 1

:stop
call :getpid
if not defined CURRENT_PID (
  echo %APP_NAME% 未运行
  if exist "%PID_FILE%" del /f /q "%PID_FILE%" >nul 2>nul
  exit /b 0
)
taskkill /f /pid !CURRENT_PID! >nul 2>nul
if exist "%PID_FILE%" del /f /q "%PID_FILE%" >nul 2>nul
echo %APP_NAME% 已停止
exit /b 0

:restart
call :stop
call :start
exit /b %errorlevel%

:status
call :getpid
if defined CURRENT_PID (
  echo %APP_NAME% 运行中，PID=!CURRENT_PID!
  exit /b 0
)
echo %APP_NAME% 未运行
exit /b 0

:logs
call :prepare || exit /b 1
if not exist "%LOG_FILE%" type nul > "%LOG_FILE%"
echo 日志文件: %LOG_FILE%
type "%LOG_FILE%"
exit /b 0

:webstart
call :prepare_ui || exit /b 1
call :getwebpid
if defined CURRENT_WEB_PID (
  echo %WEB_APP_NAME% 已在运行，PID=!CURRENT_WEB_PID!
  exit /b 0
)
start "moco-ui" /b cmd /c "cd /d \"%UI_DIR%\" && npm.cmd run dev -- --port %WEB_PORT% >> \"%WEB_LOG_FILE%\" 2>&1"
timeout /t 3 /nobreak >nul
call :getwebpid
if defined CURRENT_WEB_PID (
  > "%WEB_PID_FILE%" echo !CURRENT_WEB_PID!
  echo %WEB_APP_NAME% 启动成功，PID=!CURRENT_WEB_PID!
  echo 前端地址: http://localhost:%WEB_PORT%
  echo 日志文件: %WEB_LOG_FILE%
  exit /b 0
)
echo %WEB_APP_NAME% 启动失败，请检查日志: %WEB_LOG_FILE%
exit /b 1

:webstop
call :getwebpid
if not defined CURRENT_WEB_PID (
  echo %WEB_APP_NAME% 未运行
  if exist "%WEB_PID_FILE%" del /f /q "%WEB_PID_FILE%" >nul 2>nul
  exit /b 0
)
taskkill /f /pid !CURRENT_WEB_PID! >nul 2>nul
if exist "%WEB_PID_FILE%" del /f /q "%WEB_PID_FILE%" >nul 2>nul
echo %WEB_APP_NAME% 已停止
exit /b 0

:webrestart
call :webstop
call :webstart
exit /b %errorlevel%

:webstatus
call :getwebpid
if defined CURRENT_WEB_PID (
  echo %WEB_APP_NAME% 运行中，PID=!CURRENT_WEB_PID!，地址: http://localhost:%WEB_PORT%
  exit /b 0
)
echo %WEB_APP_NAME% 未运行
exit /b 0

:weblogs
call :prepare_ui || exit /b 1
if not exist "%WEB_LOG_FILE%" type nul > "%WEB_LOG_FILE%"
echo 日志文件: %WEB_LOG_FILE%
type "%WEB_LOG_FILE%"
exit /b 0

:depsup
call :prepare_compose || exit /b 1
%DOCKER_COMPOSE_CMD% up -d
exit /b %errorlevel%

:depsdown
call :prepare_compose || exit /b 1
%DOCKER_COMPOSE_CMD% down
exit /b %errorlevel%

:depsrestart
call :prepare_compose || exit /b 1
%DOCKER_COMPOSE_CMD% restart
exit /b %errorlevel%

:depsstatus
call :prepare_compose || exit /b 1
%DOCKER_COMPOSE_CMD% ps
exit /b %errorlevel%

:up
call :depsup || exit /b 1
call :start
if errorlevel 1 exit /b %errorlevel%
call :webstart
exit /b %errorlevel%

:down
call :webstop
call :stop
call :depsdown
exit /b %errorlevel%

:usage
echo 用法:
echo   moco.bat start
echo   moco.bat stop
echo   moco.bat restart
echo   moco.bat status
echo   moco.bat logs
echo   moco.bat web-start
echo   moco.bat web-stop
echo   moco.bat web-restart
echo   moco.bat web-status
echo   moco.bat web-logs
echo   moco.bat deps-up
echo   moco.bat deps-down
echo   moco.bat deps-restart
echo   moco.bat deps-status
echo   moco.bat up
echo   moco.bat down
echo.
echo 环境变量:
echo   JAVA_HOME  指定 JDK 17 目录
echo   JVM_OPTS   自定义 JVM 参数
echo   APP_ARGS   追加启动参数，例如 --server.port=18080
echo   WEB_PORT   前端开发服务端口，默认 18081
exit /b 1
