# --- Stage 1: Build Stage ---
FROM amazoncorretto:17-alpine-jdk AS builder
# 경로 혼동을 피하기 위해 /workspace로 변경합니다.
WORKDIR /workspace

# 빌드에 필요한 파일들 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# 소스 복사 및 빌드
COPY src src
RUN ./gradlew clean build -x test --no-daemon

# --- Stage 2: Runtime Stage ---
FROM amazoncorretto:17-alpine-jdk
WORKDIR /app

# [핵심 수정] builder의 /workspace/build/libs 폴더에서 JAR를 가져옵니다.
# Spring Boot 3는 plain jar도 생성하므로, 실행 가능한 JAR만 가져오기 위해 필터링합니다.
COPY --from=builder /workspace/build/libs/discodeit-1.2-M8.jar app.jar

# 프로젝트 정보 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

EXPOSE 80

ENTRYPOINT java -Xmx384m -jar app.jar