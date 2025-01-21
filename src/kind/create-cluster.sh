#!/bin/bash

set -x

START=$(date '+%s')

# move to src as a reference directory
pushd "$(dirname $0)/../"

# cluster setup - in kind
pushd kind
# cluster itself
kind create cluster --name demo --config ./demo-cluster-config.yaml
# ingress
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
# get back to reference directory - src
popd

# create words lists for the word dispensers and limits for the pairs generator
pushd k8s
kubectl create ns adjectives
kubectl create ns nouns
kubectl create ns number-pair
helm install system-resources . --set limits.one="$(cat adjectives.txt | wc -l)",limits.two="$(cat animals.txt | wc -l)"
popd

# build and deploy word dispenser
pushd apps/worddispenser
# maven build
gradle clean build
# docker image
pushd docker
./mkimage.sh
popd
# helm install
pushd helm
helm install word-dispenser . -n adjectives
helm install word-dispenser . -n nouns
popd
# get back to reference directory
popd

# build and deploy number pair generator
pushd apps/numberpair
# maven build
gradle clean build
# docker image
pushd docker
./mkimage.sh
popd
# helm install
pushd helm
helm install number-pair . -n number-pair
popd
# get back to reference directory
popd

# build and deploy name builder
pushd apps/namebuilder
# maven build
gradle clean build
# docker image
pushd docker
./mkimage.sh
popd
# helm install
pushd helm
helm install name-builder . -n name-builder --create-namespace
popd
# get back to reference directory
popd

# create routes - in kind
pushd kind
kubectl apply -f ./nginx-ingress-routes.yaml
# get back to reference directory
popd

END=$(date '+%s')

DURATION=$(echo "${END} - ${START}" | bc)

echo "Took ${DURATION} seconds."
