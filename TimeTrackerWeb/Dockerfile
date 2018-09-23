FROM ubuntu:bionic-20180821

RUN apt-get update
RUN apt-get install -y openjdk-8-jdk
RUN apt-get install -y unzip wget

WORKDIR /opt
RUN wget http://download.oracle.com/glassfish/4.1/release/glassfish-4.1-web.zip && mv glassfish-4.1-web.zip glassfish.zip

RUN unzip glassfish.zip && rm glassfish.zip

COPY target/timetracker.war /opt/glassfish4/glassfish/domains/domain1/autodeploy

VOLUME /opt/glassfish4/glassfish/databases
VOLUME /opt/glassfish4/glassfish/secrets

ENV PATH="/opt/glassfish4/bin:${PATH}"

CMD asadmin start-database && asadmin start-domain && asadmin create-file-user --groups TT --passwordfile /opt/glassfish4/glassfish/secrets/passwordfile tt && tail -f /opt/glassfish4/glassfish/domains/domain1/logs/server.log

EXPOSE 8080
#EXPOSE 4848
