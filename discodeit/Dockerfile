# Amazon Corretto 17 이미지를 베이스 이미지로 사용
FROM amazoncorretto:17-alpine

WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

COPY build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar

EXPOSE 80

CMD ["java", "-jar", "app.jar"]