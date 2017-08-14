#!/usr/bin/env bash

set -e

if [ "$1" == "glassfish-bundled" ]; then

  mvn -Pbundled clean install -B -V
  find ./test/ -name \*.war -exec cp {} ./glassfish5/glassfish/domains/domain1/autodeploy/ \;
  glassfish5/bin/asadmin start-domain
  sleep 120
  mvn -Pintegration -Dintegration.serverPort=8080 verify
  glassfish5/bin/asadmin stop-domain

elif [ "$1" == "glassfish-module" ]; then

  mvn -P\!bundled,module clean install -B -V
  cp ozark/target/ozark-core-*.jar ./glassfish5/glassfish/modules/
  cp jersey/target/ozark-jersey-*.jar ./glassfish5/glassfish/modules/
  cp ~/.m2/repository/javax/mvc/javax.mvc-api/1.0-SNAPSHOT/*.jar ./glassfish5/glassfish/modules/
  find ./test/ -name \*.war -exec cp {} ./glassfish5/glassfish/domains/domain1/autodeploy/ \;
  glassfish5/bin/asadmin start-domain
  sleep 120
  mvn -Pintegration -Dintegration.serverPort=8080 verify
  glassfish5/bin/asadmin stop-domain

else
  echo "Unknown test type: $1"
  exit 1;
fi
