# 1. Alpine & Slim 이미지 사용 (용량 및 보안 개선)
# 2. 버전 명시 (latest 지양)
FROM openjdk:17-jdk-slim as build

# 애플리케이션 빌드를 위한 기본 설정
WORKDIR /app

# 5. .dockerignore 파일을 활용하여 불필요한 파일을 제외
# JAR 파일을 복사
COPY ./build/libs/onetime-0.0.1-SNAPSHOT.jar app.jar

# 3. 최종 이미지: 경량화된 Alpine 이미지를 사용하여 빌드된 파일을 실행
FROM openjdk:17-jdk-alpine as final

# 애플리케이션을 실행할 디렉토리로 이동
WORKDIR /app

# 빌드된 JAR 파일을 복사하여 실행할 준비 완료
COPY --from=build /app/app.jar app.jar

# 4. HEALTHCHECK 추가
# 컨테이너가 시작된 후 5초마다, 최대 3초 동안 http://localhost:8090으로 헬스 체크
HEALTHCHECK --interval=5s --timeout=3s --start-period=30s --retries=3 \
  CMD curl --fail http://localhost:8090 || exit 1

# 3. 최종 이미지의 경량화를 위해 Multi-Stage 빌드 사용
# ENTRYPOINT 설정
ENTRYPOINT ["java", "-jar", "app.jar"]

# 서버가 동작할 포트 설정
EXPOSE 8090