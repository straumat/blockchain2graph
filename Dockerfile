# Container based on a debian Jessie with java8, neo4j, tomcat8 and blockchain2graph
FROM debian:jessie
MAINTAINER Stéphane Traumat, stephane.traumat@gmail.com

# Environment.
ENV DEBIAN_FRONTEND noninteractive

# Debian configuration & util packages installation.
RUN apt-get update && \
    # Util packages.
    apt-get install -y ca-certificates wget apt-utils && \
    # Oracle package configuration.
    echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | debconf-set-selections && \
    echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu yakkety main" | tee /etc/apt/sources.list.d/webupd8team-java-trusty.list && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys C2518248EEA14886 && \
    # Neo4j package configuration.
    wget -O - https://debian.neo4j.org/neotechnology.gpg.key | apt-key add - && \
    echo "deb http://debian.neo4j.org/repo stable/" | tee /etc/apt/sources.list.d/neo4j.list && \
    # Update.
    apt-get update

# Install and configure Java 8 from Oracle.
RUN apt-get install -y oracle-java8-installer oracle-java8-set-default

# Install and configure Neo4j.
RUN apt-get install -y neo4j=1:3.3.3 && \
    sed -i "s/#dbms.connectors.default_listen_address=0.0.0.0/dbms.connectors.default_listen_address=0.0.0.0/g" /etc/neo4j/neo4j.conf && \
    sed -i "s/#dbms.connector.bolt.listen_address=:7687/dbms.connector.bolt.listen_address=0.0.0.0:7687/g" /etc/neo4j/neo4j.conf && \
    sed -i "s/#dbms.connector.http.listen_address=:7474/dbms.connector.http.listen_address=0.0.0.0:7474/g" /etc/neo4j/neo4j.conf && \
    sed -i "s/#dbms.connector.https.listen_address=:7473/dbms.connector.https.listen_address=0.0.0.0:7473/g" /etc/neo4j/neo4j.conf && \
    sed -i "s/#dbms.security.auth_enabled=false/dbms.security.auth_enabled=false/g" /etc/neo4j/neo4j.conf

# Install and configure blockchain2graph.
ARG JAR_FILE
ADD ${JAR_FILE} /app.jar
ENV SPRING_DATA_NEO4J_URI   bolt://neo4j:neo4j@localhost:7687

# Container volume configuration.
VOLUME /var/lib/neo4j/data

# Container port configuration.
EXPOSE  747 7687 8080

# Container entry point configuration.
COPY ./docker-service-neo4j.sh /
RUN ["chmod", "+x", "/docker-service-neo4j.sh"]
COPY ./docker-service-blockchain2graph.sh /
RUN ["chmod", "+x", "/docker-service-blockchain2graph.sh"]
COPY ./docker-entrypoint.sh /
RUN ["chmod", "+x", "/docker-entrypoint.sh"]
CMD ["/docker-entrypoint.sh"]
