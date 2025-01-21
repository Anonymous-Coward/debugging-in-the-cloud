# Demo

- start cluster - check local IP in name builder values first:

        ${DEMO_DIR}/src/kind/create-cluster.sh

- `kubectl port-forward` to debug port of name builder pod - 9900

- attach debugger to localhost:9900

- set breakpoint

- perform request, show that breakpoint gets hit:

        curl -k -vvv https://localhost/name-builder/name
