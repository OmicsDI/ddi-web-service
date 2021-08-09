FROM maven:3.5.0-jdk-8-alpine as builder

WORKDIR /root
COPY ./ /root/

ADD http://ftp.ebi.ac.uk/pub/databases/omicsdi/geolite/GeoLite2-City.mmdb /root/
#RUN tar -xvf GeoLite2-City.tar.gz --strip 1

# Compilation
RUN mvn package

FROM openjdk:8-jre-alpine

COPY --from=builder /root/target/*.jar /

RUN mv /*.jar /app.jar

COPY --from=builder /root/GeoLite2-City.mmdb /opt/

ENV DDI_MAXMIND_FILE /opt/GeoLite2-City.mmdb

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
