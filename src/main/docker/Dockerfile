FROM openjdk:8-jre-alpine
VOLUME /tmp
ADD blockchain2graph.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS="-server -Xms14G -Xmx14G -XX:+UseConcMarkSweepGC"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]