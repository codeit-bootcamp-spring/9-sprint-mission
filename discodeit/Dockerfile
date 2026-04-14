# -----------------------------
# 1️⃣ Build Stage
# -----------------------------
FROM amazoncorretto:17 AS builder

WORKDIR /app

# 🔥 캐시 최적화를 위해 의존성 파일 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Gradle 실행 권한
RUN chmod +x gradlew

# 🔥 의존성 먼저 다운로드 (캐시 활용 핵심)
RUN ./gradlew dependencies --no-daemon

# 🔥 나머지 소스 복사
COPY . .

# 🔥 실제 빌드
RUN ./gradlew clean build -x test --no-daemon


# -----------------------------
# 2️⃣ Runtime Stage (Slim)
# -----------------------------
FROM amazoncorretto:17-alpine

WORKDIR /app

# 환경 변수
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 🔥 빌드 결과물만 복사 (핵심!)
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

# 포트
EXPOSE 80

# 실행
CMD ["sh", "-c", "java $JVM_OPTS -jar app.jar"]