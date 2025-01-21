#!/bin/bash

set -x

# move into dir of script
pushd `dirname $0`

WORDS_FILE="$(dirname $(dirname $(pwd)))/rsrc/adjectives-cleaned.txt"

exec java \
	-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9901 \
	-Dword.list.filepath="${WORDS_FILE}" \
	-Dserver.servlet.context-path="/adjectives" \
	-Dserver.port=8001 \
		-jar worddispenser.jar

# get back to dir from which script was started
popd
