# ==========================================
# 1단계: 빌드 환경 (Builder Stage)
# ==========================================
FROM amazoncorretto:17 AS builder

WORKDIR /app

# 1. Gradle Wrapper와 설정 파일들만 먼저 복사 (캐시 최적화의 핵심)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 2. 윈도우 줄바꿈(CRLF) 문자를 리눅스(LF)로 자동 변환 및 실행 권한 부여
RUN sed -i 's/\r$//' gradlew && chmod +x ./gradlew

# 3. 소스 코드를 복사하기 전에 의존성만 먼저 다운로드하여 캐시 레이어 생성
# (이후 소스 코드가 변경되어도 이 레이어는 캐시되어 재사용됩니다)
RUN ./gradlew dependencies --no-daemon || true

# 4. 실제 프로젝트 소스 코드 복사
COPY src src

# 5. 애플리케이션 빌드 (테스트 제외)
RUN ./gradlew clean build -x test --no-daemon

# ==========================================
# 2단계: 실행 환경 (Runtime Stage)
# ==========================================
# 실행에는 가벼운 Alpine 기반 이미지를 사용하여 최종 용량을 최소화
FROM amazoncorretto:17-alpine

WORKDIR /app

# 6. 환경 변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 7. Builder 단계에서 생성된 jar 파일만 가져와서 app.jar로 이름 변경
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar ./app.jar

# 8. 컨테이너가 사용할 포트 노출
EXPOSE 80

# 9. 애플리케이션 실행 명령어
CMD ["sh", "-c", "java $JVM_OPTS -jar app.jar"]