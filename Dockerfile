# Alpine & Slim 이미지 사용 (용량 및 보안 개선)
# 버전 명시 (latest 지양)
# 최종 이미지의 경량화를 위해 Multi-Stage 빌드 사용
FROM openjdk:17-jdk-slim as build

WORKDIR /app

# 복사할 JAR 파일의 경로
COPY ./onetime-0.0.1-SNAPSHOT.jar app.jar

# 최종 이미지: 경량화된 Alpine 이미지를 사용하여 빌드된 파일을 실행
FROM openjdk:17-jdk-alpine as final

WORKDIR /app

# Build 단계에서 JAR 파일을 복사
COPY --from=build /app/app.jar app.jar

# HEALTHCHECK 추가
# 컨테이너가 시작된 후 5초마다, 최대 3초 동안 http://localhost:8090으로 헬스 체크
HEALTHCHECK --interval=5s --timeout=3s --start-period=30s --retries=3 \
  CMD curl --fail http://localhost:8090 || exit 1

# 컨테이너 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]

# 애플리케이션이 사용하는 포트
EXPOSE 8090
