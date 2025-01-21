# Demo

- show github project: https://github.com/eldadru/ksniff

- start cluster - check local IP in name builder values first:

        ${DEMO_DIR}/src/kind/create-cluster.sh

- start ksniff on name builder pod:

        POD_NAME=$(kubectl -n name-builder get pods | grep name-builder | awk '{print $1}')
        kubectl sniff ${POD_NAME} -n name-builder -f 'tcp port 8080' -p

- tell about sniff pods not getting deleted if capture is interrupted before wireshark exits
