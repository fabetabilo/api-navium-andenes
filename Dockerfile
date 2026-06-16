FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/api-andenes-*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
