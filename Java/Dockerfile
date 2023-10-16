FROM amazoncorretto:11
ARG JAR_FILE=build/libs/*.jar

ENV REDIS_PORT=6379

COPY ${JAR_FILE} sixnumber.jar
ENTRYPOINT ["java", "-jar","sixnumber.jar"]