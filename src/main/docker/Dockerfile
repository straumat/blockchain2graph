FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD blockchain2graph.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS="-server -Xms8192m -Xmx8192m -XX:+UseConcMarkSweepGC"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]