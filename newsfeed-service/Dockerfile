FROM openjdk:21

ARG JAR_FILE=./build/libs/newsfeed-service.jar
COPY ${JAR_FILE} /app/newsfeed-service.jar

ENTRYPOINT [ "java", "-jar", "-Dspring.profiles.active=prod", "/app/newsfeed-service.jar" ]