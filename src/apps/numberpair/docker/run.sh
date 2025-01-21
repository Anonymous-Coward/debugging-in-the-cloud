#!/bin/bash

# set -x

# re-enable when needing to debug the container
# while true; do sleep 5; echo "bzzz ..."; done

# Add to parameters to enajpda/jdwp debugging on port 9900
#	-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9900

# Add to parameters to enable remote mgmt/introspection using jmx
# 	-Dcom.sun.management.jmxremote=true \
# 	-Dcom.sun.management.jmxremote.port=9090 \
# 	-Dcom.sun.management.jmxremote.rmi.port=10090 \
# 	-Dcom.sun.management.jmxremote.authenticate=false \
# 	-Dcom.sun.management.jmxremote.ssl=false \
# 	-Djava.rmi.server.hostname=localhost \

cd /opt/app

# Use the config below when you encounter problems with hazelcast and you want to get more info
# -Dhazelcast.diagnostics.enabled=true \
exec java \
	-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9900 \
	-Dserver.servlet.context-path=${BASE_PATH} \
	-Dnumber.pairs.maxOne=${MAX_ONE} \
	-Dnumber.pairs.maxTwo=${MAX_TWO} \
		-jar numberpair-0.0.1-SNAPSHOT.jar
