FROM amazoncorretto:21
LABEL authors="jclark"
MAINTAINER jarradclark.dev
VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
