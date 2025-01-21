#!/bin/bash

# java key logger agent - compile from sources because prebuilt binary is for old Java version
git clone https://github.com/jsslkeylog/jsslkeylog.git
cd jsslkeylog
mvn clean install


