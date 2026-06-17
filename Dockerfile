# --- Stage 1: Builder ---
FROM amazoncorretto:17-alpine AS builder
WORKDIR /app

# [수정] mission4 폴더 안의 설정 파일들을 현재 경로(/app)로 복사
COPY mission4/gradlew .
COPY mission4/gradle gradle
COPY mission4/build.gradle .
COPY mission4/settings.gradle .

RUN chmod +x ./gradlew
# 의존성 다운로드 (루트가 아니므로 모듈명 없이 진행 가능)
RUN ./gradlew dependencies --no-daemon

# [수정] mission4 폴더 전체를 복사
COPY mission4 .
# 빌드 실행
RUN ./gradlew bootJar --no-daemon

# --- Stage 2: Runner ---
FROM amazoncorretto:17-alpine
WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8

# [수정] 빌더 단계의 build/libs에서 바로 가져오기 (이미 WORKDIR이 /app이므로 경로 단축)
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar app.jar"]