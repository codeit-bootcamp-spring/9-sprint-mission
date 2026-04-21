# ---- Build Stage ----
FROM amazoncorretto:17 AS builder

WORKDIR /app

# 레이어 캐시 최적화: 의존성 파일을 먼저 복사해 소스 변경 시에도 캐시 재사용
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

RUN chmod +x ./gradlew \
    && ./gradlew dependencies --no-daemon

# 소스 코드 복사 후 빌드
COPY src/ src/

RUN ./gradlew clean bootJar --no-daemon

# ---- Runtime Stage ----
FROM amazoncorretto:17-alpine

LABEL authors="jun"

WORKDIR /app

ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

# builder 스테이지에서 실행 가능한 JAR만 복사
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar ./app.jar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -Dserver.port=80 -jar /app/app.jar"]
