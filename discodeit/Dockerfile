FROM gradle:7.6-jdk17 AS build
WORKDIR /home/gradle/src
COPY build.gradle settings.gradle ./
RUN gradle build --no-daemon > /dev/null 2>&1 || true
COPY . .
RUN ./gradlew clean bootJar -x test --no-daemon

FROM amazoncorretto:17-alpine
WORKDIR /app

COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

ENV JVM_OPTS=""
EXPOSE 80

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar --server.port=${PORT}"]