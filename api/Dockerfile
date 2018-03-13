FROM openjdk:8u151-jdk-alpine3.7

ENV SBT_VERSION 1.0.3
ENV SBT_HOME /usr/local/sbt
ENV SBT_URL https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.tgz
ENV PATH ${PATH}:${SBT_HOME}/bin

RUN apk add --no-cache bash bc && \
  wget -qO- ${SBT_URL} | gunzip | tar -x -C /usr/local && \
  rm -R ${SBT_HOME}/lib

WORKDIR /app
ENTRYPOINT ["sbt"]

