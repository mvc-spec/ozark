#!/bin/bash

BASEDIR=$(dirname $0)

WARS=`find . -name '*.war' -print`

for i in $WARS
do
    asadmin --passwordfile $BASEDIR/gf-password.txt undeploy `basename $i .war`
done
