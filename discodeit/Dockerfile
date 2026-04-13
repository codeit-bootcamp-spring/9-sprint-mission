FROM amazoncorretto:17-alpine AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x ./gradlew && ./gradlew build -x test --stacktrace || return 0

COPY . .
RUN ./gradlew clean bootJar -x test

FROM amazoncorretto:17-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENV JVM_OPTS="-Xmx400m -Xms200m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC"

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar app.jar"]