#!/bin/bash

# 작업 디렉토리 설정
cd /home/ubuntu

# .env 파일 로드
if [ -f "/home/ubuntu/.env" ]; then
  source /home/ubuntu/.env
else
  echo "⚠️ .env 파일을 찾을 수 없습니다. 스크립트를 종료합니다."
  exit 1
fi

# ✅ 현재 실행중인 App이 green인지 확인합니다.
IS_GREEN=$(sudo docker ps --format '{{.Names}}' | grep -w green)

# nginx 설정 파일 경로
GREEN_NGINX_CONF="/home/ubuntu/nginx/green-nginx.conf"
BLUE_NGINX_CONF="/home/ubuntu/nginx/blue-nginx.conf"
NGINX_CONF="/home/ubuntu/nginx/nginx.conf"

DOCKER_COMPOSE_FILE="/home/ubuntu/docker-compose.yaml"
HEALTH_CHECK_ENDPOINT="/"
MESSAGE_SUCCESS="⏰ [${DEPLOYMENT_GROUP_NAME}] OneTime 배포가 성공적으로 수행되었습니다!"

# 실패 시 캡처된 에러 로그를 포함하여 디스코드 메시지를 보내는 함수
send_discord_failure_message() {
  local captured_error="$1"

  if [ -z "$captured_error" ]; then
    captured_error="실패 로그를 직접 캡처하지 못했습니다. AWS CodeDeploy 콘솔을 확인해주세요."
  fi

  # JSON 형식에 맞게 이스케이프 처리
  local ERROR_LOG=$(echo "$captured_error" | sed 's/\\/\\\\/g' | sed 's/"/\\"/g' | sed ':a;N;$!ba;s/\n/\\n/g')
  local DEPLOYMENT_URL="https://ap-northeast-2.console.aws.amazon.com/codesuite/codedeploy/deployments/${DEPLOYMENT_ID}?region=ap-northeast-2"

  local JSON_PAYLOAD=$(cat <<EOF
{
  "content": "🚨 [${DEPLOYMENT_GROUP_NAME}] OneTime 배포 실패!\\n\\n**에러 로그:**\\n\`\`\`\\n${ERROR_LOG}\\n\`\`\`\\n[자세히 보기](${DEPLOYMENT_URL})"
}
EOF
)
  curl -H "Content-Type: application/json" -d "$JSON_PAYLOAD" "$DISCORD_WEBHOOK_URL"
}

# 💚 blue가 실행중이라면 green을 up합니다.
if [ -z "$IS_GREEN" ]; then
  echo "### BLUE => GREEN ###"

  echo ">>> 1. green container를 up합니다."
  DOCKER_OUTPUT=$(sudo docker compose -f "$DOCKER_COMPOSE_FILE" up --build -d green 2>&1)
  if [ $? -ne 0 ]; then
    send_discord_failure_message "$DOCKER_OUTPUT"
    exit 1
  fi

  SECONDS=0
  while true; do
    echo ">>> 2. green health check 중..."
    sleep 3
    if sudo docker exec green wget -q --spider http://localhost:8090${HEALTH_CHECK_ENDPOINT}; then
      echo "⏰ health check success!!!"
      break
    fi
    if [ $SECONDS -ge 120 ]; then
      send_discord_failure_message "Health Check 시간 초과 (120초)"
      exit 1
    fi
  done

  echo ">>> 3. nginx 라우팅 변경 및 reload"
  sudo cp "$GREEN_NGINX_CONF" "$NGINX_CONF"
  NGINX_OUTPUT=$(sudo docker exec nginx nginx -s reload 2>&1)
  if [ $? -ne 0 ]; then
    send_discord_failure_message "$NGINX_OUTPUT"
    exit 1
  fi

  echo ">>> 4. blue container를 종료합니다."
  DOCKER_OUTPUT=$(sudo docker compose -f "$DOCKER_COMPOSE_FILE" stop blue 2>&1)
  if [ $? -ne 0 ]; then
    send_discord_failure_message "$DOCKER_OUTPUT"
    exit 1
  fi

# 💙 green이 실행중이라면 blue를 up합니다.
else
  echo "### GREEN => BLUE ###"

  echo ">>> 1. blue container를 up합니다."
  DOCKER_OUTPUT=$(sudo docker compose -f "$DOCKER_COMPOSE_FILE" up --build -d blue 2>&1)
  if [ $? -ne 0 ]; then
    send_discord_failure_message "$DOCKER_OUTPUT"
    exit 1
  fi

  SECONDS=0
  while true; do
    echo ">>> 2. blue health check 중..."
    sleep 3
    if sudo docker exec blue wget -q --spider http://localhost:8090${HEALTH_CHECK_ENDPOINT}; then
      echo "⏰ health check success!!!"
      break
    fi
    if [ $SECONDS -ge 120 ]; then
      send_discord_failure_message "Health Check 시간 초과 (120초)"
      exit 1
    fi
  done

  echo ">>> 3. nginx 라우팅 변경 및 reload"
  sudo cp "$BLUE_NGINX_CONF" "$NGINX_CONF"
  NGINX_OUTPUT=$(sudo docker exec nginx nginx -s reload 2>&1)
  if [ $? -ne 0 ]; then
    send_discord_failure_message "$NGINX_OUTPUT"
    exit 1
  fi

  echo ">>> 4. green container를 종료합니다."
  DOCKER_OUTPUT=$(sudo docker compose -f "$DOCKER_COMPOSE_FILE" stop green 2>&1)
  if [ $? -ne 0 ]; then
    send_discord_failure_message "$DOCKER_OUTPUT"
    exit 1
  fi
fi

echo ">>> 5. Docker 이미지 정리"
sudo docker image prune -f
echo ">>> 6. Docker 빌드 캐시 정리"
sudo docker builder prune -f --filter "until=24h"

send_discord_success_message() {
  curl -H "Content-Type: application/json" -d "{\"content\": \"$MESSAGE_SUCCESS\"}" "$DISCORD_WEBHOOK_URL"
}
send_discord_success_message
