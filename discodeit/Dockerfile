# 1. 베이스 이미지 설정: Amazon Corretto 17 사용
FROM amazoncorretto:17

# 2. 작업 디렉토리 설정: 컨테이너 내부의 기본 경로를 /app으로 지정
WORKDIR /app

# 3. 프로젝트 파일 복사: .dockerignore에서 제외된 파일들을 뺀 나머지 전체를 복사
COPY . .

# 4. 🌟 윈도우 줄바꿈(CRLF) 문자를 리눅스(LF)로 자동 변환 (에러 해결 핵심!)
RUN sed -i 's/\r$//' gradlew

# 5. Gradle Wrapper 권한 부여 및 애플리케이션 빌드
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# 6. 컨테이너가 사용할 포트 노출
EXPOSE 80

# 7. 프로젝트 정보 및 JVM 옵션을 환경 변수로 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 8. 애플리케이션 실행 명령어
CMD ["sh", "-c", "java $JVM_OPTS -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]