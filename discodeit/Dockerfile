
FROM amazoncorretto:17-alpine-jdk AS builder

WORKDIR /workspace


COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon


COPY src src
RUN ./gradlew clean build -x test --no-daemon


FROM amazoncorretto:17-alpine-jdk
WORKDIR /app



COPY --from=builder /workspace/build/libs/discodeit-1.2-M8.jar app.jar


ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

EXPOSE 80

ENTRYPOINT java -Xmx384m -jar app.jar