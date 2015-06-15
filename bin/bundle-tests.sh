#!/bin/bash

# Tar all war files accessible from current dir
tar cvf tests.tar `find . -name '*.war' -print`
