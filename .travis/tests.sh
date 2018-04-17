#!/usr/bin/env bash

set -euo pipefail

GLASSFISH_URL="http://download.oracle.com/glassfish/5.0.1/nightly/latest-web.zip"
WILDFLY_URL="http://download.jboss.org/wildfly/11.0.0.CR1/wildfly-11.0.0.CR1.tar.gz"

if [ "${1}" == "glassfish-bundled" ]; then

  curl -s -o glassfish5.zip "${GLASSFISH_URL}"
  unzip -q glassfish5.zip
  mvn -B -V -Pbundled clean install
  find ./test/ -name \*.war -exec cp {} ./glassfish5/glassfish/domains/domain1/autodeploy/ \;
  glassfish5/bin/asadmin start-domain
  sleep 120
  mvn -Pintegration -Dintegration.serverPort=8080 verify
  glassfish5/bin/asadmin stop-domain

elif [ "${1}" == "glassfish-module" ]; then

  curl -s -o glassfish5.zip "${GLASSFISH_URL}"
  unzip -q glassfish5.zip
  mvn -B -V -P\!bundled,module clean install
  cp core/target/ozark-core-*.jar ./glassfish5/glassfish/modules/
  cp jersey/target/ozark-jersey-*.jar ./glassfish5/glassfish/modules/
  cp ~/.m2/repository/javax/mvc/javax.mvc-api/1.0-SNAPSHOT/*.jar ./glassfish5/glassfish/modules/
  find ./test/ -name \*.war -exec cp {} ./glassfish5/glassfish/domains/domain1/autodeploy/ \;
  glassfish5/bin/asadmin start-domain
  sleep 120
  mvn -Pintegration -Dintegration.serverPort=8080 verify
  glassfish5/bin/asadmin stop-domain

elif [ "${1}" == "tck-glassfish" ]; then

  curl -s -o glassfish5.zip "${GLASSFISH_URL}"
  unzip -q glassfish5.zip
  mvn -B -V -DskipTests clean install
  glassfish5/bin/asadmin start-domain
  sleep 30
  pushd tck
  mvn -B -V -Dtck-env=glassfish verify
  popd
  glassfish5/bin/asadmin stop-domain

elif [ "${1}" == "tck-wildfly" ]; then

  curl -s -o wildfly.tgz "${WILDFLY_URL}"
  tar -xzf wildfly.tgz
  mvn -B -V -DskipTests clean install
  LAUNCH_JBOSS_IN_BACKGROUND=1 JBOSS_PIDFILE=wildfly.pid ./wildfly-11.0.0.CR1/bin/standalone.sh -Dee8.preview.mode=true > wildfly.log 2>&1 &
  sleep 30
  pushd tck
  mvn -B -V -Dtck-env=wildfly verify
  popd
  kill $(cat wildfly.pid)

else
  echo "Unknown test type: $1"
  exit 1;
fi
