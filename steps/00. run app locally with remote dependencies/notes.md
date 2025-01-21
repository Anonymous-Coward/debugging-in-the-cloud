# Demo

- start cluster - check local IP in name builder values first:

        ${DEMO_DIR}/src/kind/create-cluster.sh

- port-forward to services in the cluster:

        kubectl -n nouns port-forward svc/word 15001:80
        kubectl -n adjectives port-forward svc/word 15002:80
        kubectl -n number-pair port-forward svc/numberpair 15003:80

- start name builder locally with URLs to services in cluster in config - from eclipse in debugger

        -Dserver.servlet.context-path="/name-builder"
        -Dapi.basepaths.adjectives="http://localhost:15002/adjectives"
        -Dapi.basepaths.nouns="http://localhost:15001/nouns"
        -Dapi.basepaths.numberpairs="http://localhost:15003/number-pair"

- set a breakpoint

- run request:

        curl -k -vvv https://localhost:8080/name-builder/name

- show that breakpoint is hit

- show logs of apps in cluster, to show they do get called
