#!/bin/bash

set -x

cp ../build/libs/namebuilder-0.0.1-SNAPSHOT.jar .

docker build -t "kind.local/namebuilder:0.0.1-SNAPSHOT" .

kind load docker-image -n demo "kind.local/namebuilder:0.0.1-SNAPSHOT"
