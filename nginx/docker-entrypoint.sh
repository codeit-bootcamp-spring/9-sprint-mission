#!/bin/sh
set -e

UPSTREAM_CONF=/etc/nginx/conf.d/upstream.conf
BACKEND_HOST="${BACKEND_HOST:-app}"
BACKEND_PORT="${BACKEND_PORT:-80}"

# deploy.replicas로 뜬 backend 컨테이너들은 단일 호스트명(app)에 여러 IP로 묶여있다.
# nginx의 정적 upstream server 지시어는 호스트명을 1회만 해석해 단일 주소만 사용하므로,
# 기동 시점에 직접 DNS를 조회해 모든 레플리카를 명시적인 server 라인으로 풀어써준다.
IPS=$(getent hosts "$BACKEND_HOST" | awk '{print $1}' | sort -u)

if [ -z "$IPS" ]; then
  echo "[entrypoint] WARN: ${BACKEND_HOST} 에 대한 IP를 찾지 못했습니다." >&2
fi

{
  echo "upstream backend {"
  echo "    # 로드밸런싱 전략 (택 1, 기본값: Round Robin)"
  echo "    # least_conn;"
  echo "    # ip_hash;"
  for ip in $IPS; do
    echo "    server ${ip}:${BACKEND_PORT};"
    # weight 전략 시: server ${ip}:${BACKEND_PORT} weight=3;
  done
  echo "}"
} > "$UPSTREAM_CONF"

echo "[entrypoint] upstream backend 생성 완료:"
cat "$UPSTREAM_CONF"

exec nginx -g 'daemon off;'
