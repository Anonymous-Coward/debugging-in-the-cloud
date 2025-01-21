#!/bin/bash
#!/bin/bash

set -x

# move into dir of script
pushd `dirname $0`

exec java \
	-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9904 \
	-javaagent:jSSLKeyLog.jar=name-builder-client.keys \
	-Dserver.servlet.context-path="/name-builder" \
	-Dapi.basepaths.adjectives="http://localhost:8001/adjectives" \
	-Dapi.basepaths.nouns="http://localhost:8002/nouns" \
	-Dapi.basepaths.numberpairs="https://localhost/number-pair" \
	-Dserver.port=8004 \
		-jar namebuilder.jar

# get back to dir from which script was started
popd

#	-javaagent:jSSLKeyLog.jar=names.keys \
