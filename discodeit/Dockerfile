FROM amazoncorretto:17-alpine

WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar
RUN cp build/libs/*.jar app.jar

EXPOSE 80

CMD ["java", "-jar", "app.jar"]