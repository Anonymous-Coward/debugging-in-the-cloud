#!/bin/bash

set -x

# assumes it starts in src/apps
# $1 is app to build
function buildAndCopy() {
	APP_NAME=$1
	shift
	pushd ${APP_NAME}
	# append ' | tee /dev/null' to catch all gradle output in stdout
	gradle clean build $@
	cp ./build/libs/${APP_NAME}-0.0.1-SNAPSHOT.jar ../../../steps/run-everything-locally-behind-nginx/${APP_NAME}.jar
	popd
}

# switch to this script's dir as a reference dir
pushd $(dirname $0)
# go to apps src dir
pushd ../../src/apps



buildAndCopy worddispenser $@
buildAndCopy numberpair $@
buildAndCopy namebuilder $@

popd # back to step 0 dir
popd # back to start dir

exit 0
