#!/bin/bash

set -x

cp ../build/libs/worddispenser-0.0.1-SNAPSHOT.jar .

docker build -t "kind.local/worddispenser:0.0.1-SNAPSHOT" .

kind load docker-image -n demo "kind.local/worddispenser:0.0.1-SNAPSHOT"


