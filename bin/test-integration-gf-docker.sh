#!/bin/bash

# Builds the whole project, starts a dockered nightly glassfish, deploys all test-projects and runs the integration tests.
# Expects a installed docker and a /etc/hosts entry named 'docker' pointing to your docker host.

ROOT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )"/.. && pwd )"
DEPLOYMENTS=${ROOT_DIR}/test/target/deployments
DOCKER_CONTAINER=ozark_gf5

cd ${ROOT_DIR}
mvn clean install
mkdir -p ${DEPLOYMENTS}
cd ./test
find . -name \*.war -exec cp {} ${DEPLOYMENTS}/ \;
docker run --name ${DOCKER_CONTAINER} -dit -p 4848:4848 -p 8080:8080 \
  -v ${DEPLOYMENTS}:/glassfish4/glassfish/domains/domain1/autodeploy glassfish/nightly
sleep 120
mvn -Pintegration -Dintegration.serverName=docker verify
docker stop ${DOCKER_CONTAINER}
docker rm -fv ${DOCKER_CONTAINER}
