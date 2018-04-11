                                                                                                                                                                                            # Blockchain2graph container.
FROM openjdk:8-jre-alpine
MAINTAINER Stéphane Traumat, stephane.traumat@gmail.com

# Install and configure blockchain2graph.
ARG JAR_FILE
ADD ${JAR_FILE} /app.jar

# Container port configuration.
EXPOSE 8080

# Container entry point configuration.
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]