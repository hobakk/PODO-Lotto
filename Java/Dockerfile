FROM amazoncorretto:11 AS builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM amazoncorretto:11
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} sixnumber.jar
ENTRYPOINT ["java", "-jar","sixnumber.jar"]