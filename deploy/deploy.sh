#!/bin/bash

# 작업 디렉토리 설정
cd /home/ubuntu

# ✅ 현재 실행중인 App이 green인지 확인합니다.
IS_GREEN=$(sudo docker ps --format '{{.Names}}' | grep -w green)

# nginx 설정 파일 경로
GREEN_NGINX_CONF="/etc/nginx/green-nginx.conf"
BLUE_NGINX_CONF="/etc/nginx/blue-nginx.conf"
DEFAULT_CONF="/etc/nginx/nginx.conf"

# docker-compose.yaml 경로
DOCKER_COMPOSE_FILE="/home/ubuntu/docker-compose.yaml"

# discord webhook 관련 변수
DISCORD_WEBHOOK_URL="https://discord.com/api/webhooks/1326042657880932434/ARfU0zZr8Gf1BLn1D1-qAr1pPber2FOjhKTn6fZGVxOemHL068tWt8nlQOQDhXkCFL03"
MESSAGE_SUCCESS="🥳 배포가 성공적으로 수행되었습니다!"
MESSAGE_FAILURE="🚨 배포 과정에서 오류가 발생했습니다. 빠른 확인바랍니다."

# 💬 디스코드 메시지 보내기 함수
send_discord_message() {
  local message=$1
  curl -H "Content-Type: application/json" -d "{\"content\": \"$message\"}" $DISCORD_WEBHOOK_URL
}

# 💚 blue가 실행중이라면 green을 up합니다.
if [ -z "$IS_GREEN" ]; then

  echo "### BLUE => GREEN ###"

  echo ">>> 1. green container를 up합니다."
  sudo docker compose -f "$DOCKER_COMPOSE_FILE" up -d green || {
    send_discord_message "$MESSAGE_FAILURE"
    exit 1
  }

  # Health check 타임아웃: 60초
  SECONDS=0
  while true; do
    echo ">>> 2. green health check 중..."
    sleep 3
    REQUEST=$(curl -s http://127.0.0.1:8092) # green으로 request
    if [ -n "$REQUEST" ]; then
      echo "⏰ health check success!!!"
      break
    fi
    if [ $SECONDS -ge 60 ]; then
      echo "💥 health check failed (timeout)!!!"
      send_discord_message "$MESSAGE_FAILURE"
      exit 1
    fi
  done

  echo ">>> 3. nginx를 다시 실행합니다."
  sudo cp "$GREEN_NGINX_CONF" "$DEFAULT_CONF" && sudo nginx -s reload || {
    send_discord_message "$MESSAGE_FAILURE"
    exit 1
  }

  echo ">>> 4. blue container를 down합니다."
  sudo docker compose -f "$DOCKER_COMPOSE_FILE" stop blue || {
    send_discord_message "$MESSAGE_FAILURE"
    exit 1
  }

  send_discord_message "$MESSAGE_SUCCESS"

# 💙 green이 실행중이면 blue를 up합니다.
else
  echo "### GREEN => BLUE ###"

  echo ">>> 1. blue container를 up합니다."
  sudo docker compose -f "$DOCKER_COMPOSE_FILE" up -d blue || {
    send_discord_message "$MESSAGE_FAILURE"
    exit 1
  }

  # Health check 타임아웃: 60초
  SECONDS=0
  while true; do
    echo ">>> 2. blue health check 중..."
    sleep 3
    REQUEST=$(curl -s http://127.0.0.1:8091) # blue로 request
    if [ -n "$REQUEST" ]; then
      echo "⏰ health check success!!!"
      break
    fi
    if [ $SECONDS -ge 60 ]; then
      echo "💥 health check failed (timeout)!!!"
      send_discord_message "$MESSAGE_FAILURE"
      exit 1
    fi
  done

  echo ">>> 3. nginx를 다시 실행합니다."
  sudo cp "$BLUE_NGINX_CONF" "$DEFAULT_CONF" && sudo nginx -s reload || {
    send_discord_message "$MESSAGE_FAILURE"
    exit 1
  }

  echo ">>> 4. green container를 down합니다."
  sudo docker compose -f "$DOCKER_COMPOSE_FILE" stop green || {
    send_discord_message "$MESSAGE_FAILURE"
    exit 1
  }

  send_discord_message "$MESSAGE_SUCCESS"
fi
