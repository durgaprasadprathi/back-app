FROM openjdk:11-jdk as backend_api

RUN apt-get update

RUN apt-get install -y maven

COPY pom.xml /usr/local/service/pom.xml

COPY src /usr/local/service/src

WORKDIR /usr/local/service

RUN ["mvn","clean","install","-DskipTests=true"]

FROM tomcat:9

ADD setenv.sh /usr/local/tomcat/bin/

RUN chmod u+x /usr/local/tomcat/bin/setenv.sh

CMD ["setenv.sh", "run"]

COPY --from=backend_api /usr/local/service/target/execution-module-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/qcloud.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
