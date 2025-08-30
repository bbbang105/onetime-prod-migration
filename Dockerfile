FROM amazoncorretto:17-al2-slim

WORKDIR /app

# JAR 파일만 복사
COPY ./onetime-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]

# 컨테이너가 사용하는 포트
EXPOSE 8090
