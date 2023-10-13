FROM amazoncorretto:11
ARG JAR_FILE=sixnumber-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} sixnumber.jar
ENTRYPOINT ["java", "-jar","sixnumber.jar"]