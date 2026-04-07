FROM amazoncorretto:17-al2-jdk

WORKDIR /app

COPY . .

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

RUN chmod +x ./gradlew && ./gradlew clean bootJar

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar --server.port=${PORT}"]