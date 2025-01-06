#!/bin/bash

# 작업 디렉토리 설정
cd /home/ubuntu

# 현재 실행중인 App이 green인지 확인합니다.
IS_GREEN=$(sudo docker ps --format '{{.Names}}' | grep -w green)

# nginx 설정 파일 경로
GREEN_NGINX_CONF="/etc/nginx/green-nginx.conf"
BLUE_NGINX_CONF="/etc/nginx/blue-nginx.conf"
DEFAULT_CONF="/etc/nginx/nginx.conf"

# docker-compose.yaml 경로
DOCKER_COMPOSE_FILE="/home/ubuntu/docker-compose.yaml"

# blue가 실행중이라면 green을 up합니다.
if [ -z "$IS_GREEN" ]; then
  echo "### BLUE => GREEN ###"

  echo ">>> 1. green container를 up합니다."
  sudo docker compose -f $DOCKER_COMPOSE_FILE up -d green

  while true; do
    echo ">>> 2. green health check 중..."
    sleep 3
    REQUEST=$(curl -s http://127.0.0.1:8092) # green으로 request
    if [ -n "$REQUEST" ]; then
      echo "⏰ health check success!!!"
      break
    fi
  done

  echo ">>> 3. nginx를 다시 실행합니다."
  sudo cp $GREEN_NGINX_CONF $DEFAULT_CONF
  sudo nginx -s reload

  echo ">>> 4. blue container를 down합니다."
  sudo docker compose -f $DOCKER_COMPOSE_FILE stop blue

# green이 실행중이면 blue를 up합니다.
else
  echo "### GREEN => BLUE ###"

  echo ">>> 1. blue container를 up합니다."
  sudo docker compose -f $DOCKER_COMPOSE_FILE up -d blue

  while true; do
    echo ">>> 2. blue health check 중..."
    sleep 3
    REQUEST=$(curl -s http://127.0.0.1:8091) # blue로 request
    if [ -n "$REQUEST" ]; then
      echo "⏰ health check success!!!"
      break
    fi
  done

  echo ">>> 3. nginx를 다시 실행합니다."
  sudo cp $BLUE_NGINX_CONF $DEFAULT_CONF
  sudo nginx -s reload

  echo ">>> 4. green container를 down합니다."
  sudo docker compose -f $DOCKER_COMPOSE_FILE stop green
fi
