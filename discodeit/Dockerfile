FROM amazoncorretto:17 AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

COPY . .
RUN ./gradlew clean build -x test --no-daemon

FROM amazoncorretto:17-alpine AS runtime
WORKDIR /app

COPY --from=build /app/build/libs/discodeit-*.jar app.jar

ENV JVM_OPTS=""
EXPOSE 8080

CMD ["sh", "-c", "java $JVM_OPTS -jar app.jar"]