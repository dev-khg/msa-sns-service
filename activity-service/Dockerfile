FROM openjdk:21

ARG JAR_FILE=./build/libs/activity-service.jar
COPY ${JAR_FILE} /app/activity-service.jar

ENTRYPOINT [ "java", "-jar","-Dspring.profiles.active=prod","/app/activity-service.jar" ]