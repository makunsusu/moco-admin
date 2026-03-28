#!/bin/sh

set -eu

APP_NAME="moco-admin"
BASE_DIR=$(CDPATH= cd -- "$(dirname "$0")" && pwd)
JAR_PATH="${BASE_DIR}/moco-admin/target/${APP_NAME}.jar"
JAR_NAME=$(basename "${JAR_PATH}")
UI_DIR="${BASE_DIR}/moco-ui"
UI_PACKAGE_JSON="${UI_DIR}/package.json"
LOG_DIR="${BASE_DIR}/logs"
PID_DIR="${BASE_DIR}/runtime"
PID_FILE="${PID_DIR}/${APP_NAME}.pid"
LOG_FILE="${LOG_DIR}/${APP_NAME}.log"
WEB_APP_NAME="moco-ui"
WEB_PID_FILE="${PID_DIR}/${WEB_APP_NAME}.pid"
WEB_LOG_FILE="${LOG_DIR}/${WEB_APP_NAME}.log"
COMPOSE_FILE="${BASE_DIR}/docker-compose.yml"

JAVA_CMD=""
JVM_OPTS=${JVM_OPTS:-"-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError"}
APP_ARGS=${APP_ARGS:-""}
WEB_PORT=${WEB_PORT:-"18081"}
DOCKER_COMPOSE_CMD=""

usage() {
  cat <<EOF
用法:
  ./moco.sh start         启动后端服务
  ./moco.sh stop          停止后端服务
  ./moco.sh restart       重启后端服务
  ./moco.sh status        查看后端服务状态
  ./moco.sh logs          查看后端日志输出
  ./moco.sh web-start     启动前端开发服务
  ./moco.sh web-stop      停止前端开发服务
  ./moco.sh web-restart   重启前端开发服务
  ./moco.sh web-status    查看前端开发服务状态
  ./moco.sh web-logs      查看前端开发日志输出
  ./moco.sh deps-up       启动 MySQL / Redis 依赖
  ./moco.sh deps-down     停止 MySQL / Redis 依赖
  ./moco.sh deps-restart  重启 MySQL / Redis 依赖
  ./moco.sh deps-status   查看 MySQL / Redis 依赖状态
  ./moco.sh up            启动依赖、后端和前端
  ./moco.sh down          停止前端、后端并停止依赖

环境变量:
  JAVA_HOME   指定 JDK 目录，要求 JDK 17+
  JVM_OPTS    自定义 JVM 参数
  APP_ARGS    追加应用启动参数，例如: --server.port=18080
  WEB_PORT    前端开发服务端口，默认 18081
EOF
}

ensure_dirs() {
  mkdir -p "${LOG_DIR}" "${PID_DIR}"
}

check_docker_compose() {
  if [ ! -f "${COMPOSE_FILE}" ]; then
    echo "未找到 ${COMPOSE_FILE}"
    exit 1
  fi

  if docker compose version >/dev/null 2>&1; then
    DOCKER_COMPOSE_CMD="docker compose"
  elif command -v docker-compose >/dev/null 2>&1; then
    DOCKER_COMPOSE_CMD="docker-compose"
  else
    echo "未找到 docker compose，请先安装 Docker Compose。"
    exit 1
  fi
}

check_java() {
  java_candidates=""
  if [ -n "${JAVA_HOME:-}" ] && [ -x "${JAVA_HOME}/bin/java" ]; then
    java_candidates="${JAVA_HOME}/bin/java"
  fi
  if [ -x "/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home/bin/java" ]; then
    java_candidates="${java_candidates}
/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home/bin/java"
  fi
  if command -v java >/dev/null 2>&1; then
    java_candidates="${java_candidates}
$(command -v java)"
  fi

  JAVA_CMD=""
  for candidate in ${java_candidates}; do
    [ -x "${candidate}" ] || continue
    java_version=$("${candidate}" -version 2>&1 | awk -F '"' '/version/ {print $2; exit}')
    java_major=$(printf '%s' "${java_version}" | awk -F. '{if ($1 == 1) print $2; else print $1}')
    if [ -n "${java_major}" ] && [ "${java_major}" -ge 17 ]; then
      JAVA_CMD="${candidate}"
      break
    fi
  done

  if [ -z "${JAVA_CMD}" ]; then
    echo "未找到 java，请先安装 JDK 17 并设置 JAVA_HOME。"
    exit 1
  fi
}

check_jar() {
  if [ ! -f "${JAR_PATH}" ]; then
    echo "未找到 ${JAR_PATH}"
    echo "请先在项目根目录执行: mvn clean package -DskipTests"
    exit 1
  fi
}

check_ui() {
  if [ ! -f "${UI_PACKAGE_JSON}" ]; then
    echo "未找到 ${UI_PACKAGE_JSON}"
    exit 1
  fi
  if [ ! -d "${UI_DIR}/node_modules" ]; then
    echo "未找到 ${UI_DIR}/node_modules"
    echo "请先执行: cd moco-ui && npm install"
    exit 1
  fi
  if ! command -v npm >/dev/null 2>&1; then
    echo "未找到 npm，请先安装 Node.js 18+ 和 npm 9+。"
    exit 1
  fi
}

get_pid() {
  if [ -f "${PID_FILE}" ]; then
    pid=$(cat "${PID_FILE}" 2>/dev/null || true)
    if [ -n "${pid:-}" ] && kill -0 "${pid}" 2>/dev/null; then
      echo "${pid}"
      return 0
    fi
    rm -f "${PID_FILE}"
  fi

  pid=$(pgrep -f "${JAR_PATH}" 2>/dev/null || true)
  if [ -z "${pid}" ]; then
    pid=$(pgrep -f "[/]${JAR_NAME}" 2>/dev/null || true)
  fi
  if [ -n "${pid}" ]; then
    echo "${pid}" | awk 'NR==1 {print $1}'
    return 0
  fi
  return 1
}

get_web_pid() {
  if [ -f "${WEB_PID_FILE}" ]; then
    pid=$(cat "${WEB_PID_FILE}" 2>/dev/null || true)
    if [ -n "${pid:-}" ] && kill -0 "${pid}" 2>/dev/null; then
      echo "${pid}"
      return 0
    fi
    rm -f "${WEB_PID_FILE}"
  fi

  pid=$(pgrep -f "${UI_DIR}.*vue-cli-service serve" 2>/dev/null || true)
  if [ -z "${pid}" ]; then
    pid=$(pgrep -f "vue-cli-service serve.*${WEB_PORT}" 2>/dev/null || true)
  fi
  if [ -n "${pid}" ]; then
    echo "${pid}" | awk 'NR==1 {print $1}'
    return 0
  fi
  return 1
}

start() {
  ensure_dirs
  check_java
  check_jar

  if pid=$(get_pid); then
    echo "${APP_NAME} 已在运行，PID=${pid}"
    return 0
  fi

  nohup "${JAVA_CMD}" ${JVM_OPTS} -jar "${JAR_PATH}" ${APP_ARGS} >> "${LOG_FILE}" 2>&1 &
  echo $! > "${PID_FILE}"
  sleep 2

  if pid=$(get_pid); then
    echo "${APP_NAME} 启动成功，PID=${pid}"
    echo "日志文件: ${LOG_FILE}"
  else
    echo "${APP_NAME} 启动失败，请检查日志: ${LOG_FILE}"
    return 1
  fi
}

stop() {
  if ! pid=$(get_pid); then
    echo "${APP_NAME} 未运行"
    return 0
  fi

  kill "${pid}" 2>/dev/null || true
  count=0
  while kill -0 "${pid}" 2>/dev/null; do
    count=$((count + 1))
    if [ "${count}" -ge 15 ]; then
      kill -9 "${pid}" 2>/dev/null || true
      break
    fi
    sleep 1
  done

  rm -f "${PID_FILE}"
  echo "${APP_NAME} 已停止"
}

status() {
  if pid=$(get_pid); then
    echo "${APP_NAME} 运行中，PID=${pid}"
  else
    echo "${APP_NAME} 未运行"
  fi
}

logs() {
  ensure_dirs
  touch "${LOG_FILE}"
  tail -n 100 -f "${LOG_FILE}"
}

web_start() {
  ensure_dirs
  check_ui

  if pid=$(get_web_pid); then
    echo "${WEB_APP_NAME} 已在运行，PID=${pid}"
    return 0
  fi

  (
    cd "${UI_DIR}"
    nohup npm run dev -- --port "${WEB_PORT}" >> "${WEB_LOG_FILE}" 2>&1 &
    echo $! > "${WEB_PID_FILE}"
  )
  sleep 3

  if pid=$(get_web_pid); then
    echo "${WEB_APP_NAME} 启动成功，PID=${pid}"
    echo "前端地址: http://localhost:${WEB_PORT}"
    echo "日志文件: ${WEB_LOG_FILE}"
  else
    echo "${WEB_APP_NAME} 启动失败，请检查日志: ${WEB_LOG_FILE}"
    return 1
  fi
}

web_stop() {
  if ! pid=$(get_web_pid); then
    echo "${WEB_APP_NAME} 未运行"
    return 0
  fi

  kill "${pid}" 2>/dev/null || true
  count=0
  while kill -0 "${pid}" 2>/dev/null; do
    count=$((count + 1))
    if [ "${count}" -ge 15 ]; then
      kill -9 "${pid}" 2>/dev/null || true
      break
    fi
    sleep 1
  done

  rm -f "${WEB_PID_FILE}"
  echo "${WEB_APP_NAME} 已停止"
}

web_status() {
  if pid=$(get_web_pid); then
    echo "${WEB_APP_NAME} 运行中，PID=${pid}，地址: http://localhost:${WEB_PORT}"
  else
    echo "${WEB_APP_NAME} 未运行"
  fi
}

web_logs() {
  ensure_dirs
  touch "${WEB_LOG_FILE}"
  tail -n 100 -f "${WEB_LOG_FILE}"
}

deps_up() {
  check_docker_compose
  ${DOCKER_COMPOSE_CMD} up -d
}

deps_down() {
  check_docker_compose
  ${DOCKER_COMPOSE_CMD} down
}

deps_restart() {
  check_docker_compose
  ${DOCKER_COMPOSE_CMD} restart
}

deps_status() {
  check_docker_compose
  ${DOCKER_COMPOSE_CMD} ps
}

up() {
  deps_up
  start
  web_start
}

down() {
  web_stop
  stop
  deps_down
}

case "${1:-}" in
  start) start ;;
  stop) stop ;;
  restart) stop; start ;;
  status) status ;;
  logs) logs ;;
  web-start) web_start ;;
  web-stop) web_stop ;;
  web-restart) web_stop; web_start ;;
  web-status) web_status ;;
  web-logs) web_logs ;;
  deps-up) deps_up ;;
  deps-down) deps_down ;;
  deps-restart) deps_restart ;;
  deps-status) deps_status ;;
  up) up ;;
  down) down ;;
  *) usage; exit 1 ;;
esac
