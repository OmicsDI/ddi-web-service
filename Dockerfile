FROM maven:3.5.0-jdk-8-alpine as builder

WORKDIR /root
COPY ./ /root/

ADD https://geolite.maxmind.com/download/geoip/database/GeoLite2-City.tar.gz /root/
RUN tar -xvf GeoLite2-City.tar.gz --strip 1

# Compilation
RUN mvn package

# Use a minimal image as parent
FROM openjdk:8-jdk-alpine

# Environment variables
ENV TOMCAT_MAJOR=8 \
    TOMCAT_VERSION=8.5.37 \
    CATALINA_HOME=/opt/tomcat

# init
RUN apk -U upgrade --update && \
    apk add curl && \
    apk add ttf-dejavu

RUN mkdir -p /opt

# install tomcat
RUN curl -jkSL -o /tmp/apache-tomcat.tar.gz http://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR}/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    gunzip /tmp/apache-tomcat.tar.gz && \
    tar -C /opt -xf /tmp/apache-tomcat.tar && \
    ln -s /opt/apache-tomcat-$TOMCAT_VERSION $CATALINA_HOME

# cleanup
RUN apk del curl && \
    rm -rf /tmp/* /var/cache/apk/*

RUN rm -rf $CATALINA_HOME/webapps/ROOT

# Fetch jar created into the previous step
COPY --from=builder /root/target/*.war $CATALINA_HOME/webapps/ROOT.war
COPY --from=builder /root/GeoLite2-City.mmdb /opt/

ENV DDI_MAXMIND_FILE /opt/GeoLite2-City.mmdb

EXPOSE 8080

CMD $CATALINA_HOME/bin/catalina.sh run
