# Blockchain2graph benchmark.                                                                                                                                                                                     # Blockchain2graph container.
FROM openjdk:8-jre-alpine
MAINTAINER Stéphane Traumat, stephane.traumat@gmail.com

# Environment variables.
ENV SPRING_MAIL_HOST=SSL0.OVH.NET
ENV SPRING_MAIL_PORT=587
ENV SPRING_MAIL_USERNAME=benchmark-blockchain2graph@scub.net
ENV SPRING_DATA_NEO4J_URI=file:///graph.db

# Install and configure blockchain2graph.
ARG JAR_FILE
ADD ${JAR_FILE} /app.jar

# Container port configuration.
EXPOSE 8080

# Container entry point configuration.
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar", "--benchmark"]