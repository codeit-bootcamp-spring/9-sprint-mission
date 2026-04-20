# ===== 1단계: build stage =====
FROM gradle:8-jdk17 AS build
WORKDIR /app

# 1. 의존성 파일 복사 (현재 위치가 이미 discodeit 폴더 안이므로 그대로 복사)
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# 2. 의존성 다운로드 (캐시 활용)
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

# 3. 소스 코드 복사
COPY src ./src

# 4. 빌드 (테스트 제외 및 빌드 실행)
# plain jar 생성을 방지하기 위해 build.gradle에 jar { enabled = false } 설정을 권장합니다.
RUN ./gradlew clean build -x test --no-daemon

# ===== 2단계: runtime stage (이미지 최적화 - local-slim) =====
FROM amazoncorretto:17-alpine-jdk
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

# 컨테이너 포트 설정
EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]