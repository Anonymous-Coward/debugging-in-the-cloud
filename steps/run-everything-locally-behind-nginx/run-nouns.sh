#!/bin/bash

set -x

# move into dir of script
pushd `dirname $0`

WORDS_FILE="$(dirname $(dirname $(pwd)))/rsrc/animals-cleaned.txt"

exec java \
	-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9902 \
	-Dword.list.filepath="${WORDS_FILE}" \
	-Dserver.servlet.context-path="/nouns" \
	-Dserver.port=8002 \
		-jar worddispenser.jar

# get back to dir from which script was started
popd
