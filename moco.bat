@echo off
setlocal enabledelayedexpansion

set "APP_NAME=moco-admin"
set "BASE_DIR=%~dp0"
set "JAR_PATH=%BASE_DIR%moco-admin\target\%APP_NAME%.jar"
set "LOG_DIR=%BASE_DIR%logs"
set "PID_DIR=%BASE_DIR%runtime"
set "PID_FILE=%PID_DIR%\%APP_NAME%.pid"
set "LOG_FILE=%LOG_DIR%\%APP_NAME%.log"

if defined JAVA_HOME (
  set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
) else (
  set "JAVA_CMD=java"
)

if not defined JVM_OPTS (
  set "JVM_OPTS=-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError"
)

if "%~1"=="" goto :usage
if /I "%~1"=="start" goto :start
if /I "%~1"=="stop" goto :stop
if /I "%~1"=="restart" goto :restart
if /I "%~1"=="status" goto :status
if /I "%~1"=="logs" goto :logs
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

:usage
echo 用法:
echo   moco.bat start
echo   moco.bat stop
echo   moco.bat restart
echo   moco.bat status
echo   moco.bat logs
echo.
echo 环境变量:
echo   JAVA_HOME  指定 JDK 17 目录
echo   JVM_OPTS   自定义 JVM 参数
echo   APP_ARGS   追加启动参数，例如 --server.port=18080
exit /b 1
