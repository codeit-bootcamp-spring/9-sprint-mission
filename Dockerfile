# Stage 1: 빌드
FROM amazoncorretto:17 AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew clean build -x test

# Stage 2: 실행
FROM amazoncorretto:17
WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

COPY --from=build /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]
