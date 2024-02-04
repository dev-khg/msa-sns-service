FROM openjdk:21

ARG JAR_FILE=./build/libs/user-service.jar
COPY ${JAR_FILE} /app/user-service.jar

ENTRYPOINT [ "java", "-jar", "-Dspring.profiles.active=prod","/app/user-service.jar" ]