FROM openjdk:21

ARG JAR_FILE=./build/libs/gateway-service.jar
COPY ${JAR_FILE} /app/gateway-service.jar

ENTRYPOINT [ "java", "-jar","-Dspring.profiles.active=prod", "/app/gateway-service.jar" ]