FROM adoptopenjdk/openjdk11:jre-11.0.7_10-ubuntu

RUN echo "Docker build starts. Using adoptopenjdk/openjdk11:jre-11.0.7_10-ubuntu as base image"

RUN echo "maintained by : Abhiroop Ghatak => ghatak.20@gmail.com"

RUN apt install curl

ARG CI_COMMIT_SHA

ARG NEXUS_HOST

ARG NEXUS_REPO_USER

ARG NEXUS_REPO_PASS

WORKDIR /usr/app

RUN curl  -u $NEXUS_REPO_USER:$NEXUS_REPO_PASS -L -o view-service-0.0.1-SNAPSHOT.jar "http://NEXUS_HOST/service/rest/v1/search/assets/download?sort=version&repository=maven-snapshots&maven.groupId=com.abhiroop.mybazaar&maven.artifactId=view-service&maven.baseVersion=$CI_COMMIT_SHA-SNAPSHOT&maven.extension=jar"

RUN echo "Artifacts downloaded from binary repository Nexus"
EXPOSE 8080
CMD java -jar view-service-0.0.1-SNAPSHOT.jar

USER 1009
