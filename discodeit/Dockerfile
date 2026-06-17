# 빌드 스테이지
FROM amazoncorretto:17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper 파일 먼저 복사
COPY gradle ./gradle
COPY gradlew ./gradlew

# Gradle 캐시를 위한 의존성 파일 복사
COPY build.gradle settings.gradle ./

# 의존성 다운로드
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사 및 빌드
COPY src ./src
RUN ./gradlew build -x test --no-daemon


# 런타임 스테이지
FROM amazoncorretto:17-alpine3.21

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 정보를 ENV로 설정
ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

# 빌드 스테이지에서 jar 파일만 복사
# jar 파일명을 와일드카드로 복사 — 버전 변경에 유연하게 대응
COPY --from=builder /app/build/libs/*.jar app.jar

# 8080 포트 노출 - Nginx설정을 위해 80에서 변경
EXPOSE 8080

# jar 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]