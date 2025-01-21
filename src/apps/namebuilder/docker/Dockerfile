FROM eclipse-temurin:17.0.5_8-jdk

COPY namebuilder-0.0.1-SNAPSHOT.jar /opt/app/
COPY run.sh /opt/app/
COPY jSSLKeyLog.jar /opt/app

RUN apt-get update; \
    apt-get upgrade; \
    apt-get install -y nano curl net-tools dnsutils wget bash less socat zip unzip procps dos2unix; \
    useradd -M -U app; \
    dos2unix /opt/app/run.sh; \
    chown -R app:app /opt/app; \
    chmod -R u+rwx /opt/app;

EXPOSE 8080

USER app

CMD /opt/app/run.sh
