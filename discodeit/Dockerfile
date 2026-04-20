# ==========================================
# 1단계 (빌드 단계 - builder)
# ==========================================
FROM amazoncorretto:17 AS builder
WORKDIR /app

# 레이어 캐싱
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# 이제 진짜 소스 코드(src)를 복사하고 빌드합니다.
COPY src src
RUN ./gradlew build -x test --no-daemon

# ==========================================
# 2단계 (런타임 단계)
# ==========================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""
EXPOSE 80

# 1단계(builder)에서 완성된 jar 파일만 빼와서 'app.jar'라는 이름으로 가져옵니다.
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

CMD ["sh", "-c", "java $JVM_OPTS -jar app.jar"]