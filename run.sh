#!/bin/sh
#mvn versions:update-properties
mkdir -p ./logs
(
export JENKINS_ARGS="--requestHeaderSize=32768"
mvn hpi:run -Djava.util.logging.config.file=logging.properties \
    -Denforcer.skip -Dasciidoctor.skip -Dmaven.javadoc.skip -DskipTests \
    -Djetty.port=8070 \

)

