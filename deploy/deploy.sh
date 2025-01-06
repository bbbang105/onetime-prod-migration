#!/bin/bash

# 현재 실행중인 App이 green인지 확인합니다.
IS_GREEN=$(sudo docker ps --format '{{.Names}}' | grep -w green)
DEFAULT_CONF="/etc/nginx/nginx.conf"

# blue가 실행중이라면 green을 up합니다.
if [ -z "$IS_GREEN" ]; then

  echo "### BLUE => GREEN ###"

  echo ">>> 1. green container를 up합니다."
  sudo docker compose up -d green

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
  sudo cp /etc/nginx/green-nginx.conf /etc/nginx/nginx.conf
  sudo nginx -s reload

  echo ">>> 4. blue container를 down합니다."
  sudo docker compose stop blue

# green이 실행중이면 blue를 up합니다.
else
  echo "### GREEN => BLUE ###"

  echo ">>> 1. blue container up합니다."
  sudo docker compose up -d blue

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
  sudo cp /etc/nginx/blue-nginx.conf /etc/nginx/nginx.conf
  sudo nginx -s reload

  echo ">>> 4. green container를 down합니다."
  sudo docker compose stop green
fi
