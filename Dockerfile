# 빌드 스테이지
FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY gradle ./gradle
COPY gradlew ./gradlew
COPY build.gradle settings.gradle ./

RUN ./gradlew dependencies

COPY src ./src
RUN ./gradlew build -x test

# 런타임 스테이지
FROM amazoncorretto:17-alpine3.21

WORKDIR /app

ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar ./

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar ${PROJECT_NAME}-${PROJECT_VERSION}.jar"]