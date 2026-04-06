# 1. Amazon Corretto 17 베이스 이미지
FROM amazoncorretto:17

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 환경 변수 설정 (프로젝트 정보 + JVM 옵션)
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 4. 프로젝트 파일 복사 (.dockerignore 적용됨)
COPY . .

# 5. Gradle Wrapper로 빌드
RUN chmod +x ./gradlew && \
    ./gradlew clean build -x test

# 6. 80 포트 노출
EXPOSE 80

# 7. 애플리케이션 실행
CMD ["sh", "-c", "java $JVM_OPTS -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]