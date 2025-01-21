#!/bin/bash

set -x

cp ../build/libs/numberpair-0.0.1-SNAPSHOT.jar .

docker build -t "kind.local/numberpair:0.0.1-SNAPSHOT" .

kind load docker-image -n demo "kind.local/numberpair:0.0.1-SNAPSHOT"
