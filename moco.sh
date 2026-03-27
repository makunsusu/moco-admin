#!/bin/sh

set -eu

APP_NAME="moco-admin"
BASE_DIR=$(CDPATH= cd -- "$(dirname "$0")" && pwd)
JAR_PATH="${BASE_DIR}/moco-admin/target/${APP_NAME}.jar"
JAR_NAME=$(basename "${JAR_PATH}")
LOG_DIR="${BASE_DIR}/logs"
PID_DIR="${BASE_DIR}/runtime"
PID_FILE="${PID_DIR}/${APP_NAME}.pid"
LOG_FILE="${LOG_DIR}/${APP_NAME}.log"

JAVA_CMD=""
JVM_OPTS=${JVM_OPTS:-"-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError"}
APP_ARGS=${APP_ARGS:-""}

usage() {
  cat <<EOF
用法:
  ./moco.sh start     启动后端服务
  ./moco.sh stop      停止后端服务
  ./moco.sh restart   重启后端服务
  ./moco.sh status    查看服务状态
  ./moco.sh logs      查看日志输出

环境变量:
  JAVA_HOME   指定 JDK 目录，要求 JDK 17+
  JVM_OPTS    自定义 JVM 参数
  APP_ARGS    追加应用启动参数，例如: --server.port=18080
EOF
}

ensure_dirs() {
  mkdir -p "${LOG_DIR}" "${PID_DIR}"
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

start() {
  ensure_dirs
  check_java
  check_jar

  if pid=$(get_pid); then
    echo "${APP_NAME} 已在运行，PID=${pid}"
    exit 0
  fi

  nohup "${JAVA_CMD}" ${JVM_OPTS} -jar "${JAR_PATH}" ${APP_ARGS} >> "${LOG_FILE}" 2>&1 &
  echo $! > "${PID_FILE}"
  sleep 2

  if pid=$(get_pid); then
    echo "${APP_NAME} 启动成功，PID=${pid}"
    echo "日志文件: ${LOG_FILE}"
  else
    echo "${APP_NAME} 启动失败，请检查日志: ${LOG_FILE}"
    exit 1
  fi
}

stop() {
  if ! pid=$(get_pid); then
    echo "${APP_NAME} 未运行"
    exit 0
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

case "${1:-}" in
  start) start ;;
  stop) stop ;;
  restart) stop; start ;;
  status) status ;;
  logs) logs ;;
  *) usage; exit 1 ;;
esac
