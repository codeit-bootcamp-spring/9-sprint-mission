FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY gradle ./gradle
COPY gradlew build.gradle settings.gradle ./

RUN sed -i 's/\r$//' gradlew && chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || true

COPY src ./src
RUN ./gradlew clean bootJar -x test --no-daemon

FROM amazoncorretto:17-alpine

WORKDIR /app

ENV JVM_OPTS=""

COPY --from=builder /app/build/libs/*.jar ./app.jar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]
