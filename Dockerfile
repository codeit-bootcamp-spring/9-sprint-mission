# 1. 베이스 이미지
FROM amazoncorretto:17

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 프로젝트 파일 복사
COPY . .
RUN chmod +x gradlew

# 4. Gradle로 빌드
RUN ./gradlew clean build -x test

# 5. 포트 노출
EXPOSE 80

# 6. 환경 변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 7. 애플리케이션 실행
CMD java $JVM_OPTS -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar