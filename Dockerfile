FROM amazoncorretto:17-alpine3.17

ARG JAR_VERSION
COPY build/libs/matrix-animator-api-${JAR_VERSION}.jar /app.jar

CMD ["java", "-jar", "app.jar"]