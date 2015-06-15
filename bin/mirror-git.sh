#!/bin/bash

cd /tmp
git clone --bare ssh://spericas@git.java.net/ozark~sources
cd ozark~sources.git
git push --mirror https://github.com/spericas/ozark.git
cd ..
rm -rf ozark~sources.git

