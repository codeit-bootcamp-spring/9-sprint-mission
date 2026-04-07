FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY . /app

RUN chmod +x ./gradlew \
    && ./gradlew clean bootJar --no-daemon

FROM amazoncorretto:17

LABEL authors="jun"

WORKDIR /app

ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

# builder 스테이지에서 실행 가능한 JAR만 복사
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar ./app.jar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -Dserver.port=80 -jar /app/app.jar"]
