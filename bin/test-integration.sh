#!/bin/bash

BASEDIR=$(dirname $0)

source $BASEDIR/deploy-tests-gf.sh

cd $BASEDIR/../test
mvn -Pintegration verify
