#!/bin/sh

set -x

# move into dir of script
pushd `dirname $0`

export ADJECTIVES_FILE="$(dirname $(dirname $(pwd)))/rsrc/adjectives-cleaned.txt"
export NOUNS_FILE="$(dirname $(dirname $(pwd)))/rsrc/animals-cleaned.txt"

MAX_ONE=$(cat ${NOUNS_FILE} | wc -l)
MAX_TWO=$(cat ${ADJECTIVES_FILE} | wc -l)

exec java \
	-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9903 \
	-Dserver.servlet.context-path="/number-pair" \
	-Dnumber.pairs.maxOne="${MAX_ONE}" \
	-Dnumber.pairs.maxTwo="${MAX_TWO}" \
	-Dserver.port=8003 \
		-jar numberpair.jar

# get back to dir from which script was started
popd
