#  build stage
FROM amazoncorretto:17 AS builder

WORKDIR /app

# gradle 관련 먼저 복사
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew

# 의존성 먼저 다운 (캐시됨)
RUN ./gradlew dependencies --no-daemon

# 소스코드 복사
COPY src ./src

# 빌드
RUN ./gradlew clean bootJar --no-daemon


# runtime stage (가볍게!)
FROM amazoncorretto:17-alpine

WORKDIR /app

ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

# jar만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar app.jar"]