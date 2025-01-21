# Demo

- start cluster - check local IP in name builder values first:

        ${DEMO_DIR}/src/kind/create-cluster.sh

- port-forward to services in the cluster to make them available from local:

        kubectl -n nouns port-forward svc/word 15001:80
        kubectl -n adjectives port-forward svc/word 15002:80
        kubectl -n number-pair port-forward svc/numberpair 15003:80

- start name builder locally from command line, with configuration to hit cluster and remote debugging enabled - see step 00:

        cd ${DEMO_DIR}/src/apps/namebuilder
        java -jar \
            -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9904 \
            -Dserver.servlet.context-path="/name-builder" \
            -Dapi.basepaths.adjectives="https://localhost:15002/adjectives" \
            -Dapi.basepaths.nouns="https://localhost:15001/nouns" \
            -Dapi.basepaths.numberpairs="https://localhost:15003/number-pair" \
            -Dserver.port=8004 \
                ./build/libs/namebuilder-0.0.1-SNAPSHOT.jar


- attach debugger to debugging port - 9904 - and set breakpoint

- perform request, show that breakpoint gets hit:

        curl -k -vvv https://localhost:8004/name-builder/name

