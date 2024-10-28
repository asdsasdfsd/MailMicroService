FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/MailMicroService-0.1.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8081
