FROM openjdk:17

ARG JAR_FILE=/build/libs/pre-order-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /pre-order.jar
EXPOSE 8081

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod", "/pre-order.jar"]