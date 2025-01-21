# Demo

- start cluster - check local IP in name builder values first:

        ${DEMO_DIR}/src/kind/create-cluster.sh

- tmux, two vertical panes

- in left pane, start an ephemeral container in a pod:

       POD_NAME=$(kubectl -n name-builder get pods | grep name-builder | awk '{print $1}')
       kubectl -n name-builder debug -it pod/${POD_NAME} --image=alpine -- sh

- add tcpdump and start it:

       apk update && apk add tcpdump
       tcpdump -i any -w /tmp/packets.pcap --immediate-mode 'tcp port 8080'

- perform multiple requests in right pane

- in right pane, copy pcap file locally:

       POD_NAME=$(kubectl -n name-builder get pods | grep name-builder | awk '{print $1}')
       describe pod $POD_NAME # will output debug container name

       kubectl -n name-builder -c <debug container name> cp \
           $(k -n name-builder get pods | awk '{print $1}'):/tmp/packets.pcap ./packets.pcap

- open copied `packets.pcap` with wireshark

- find and follow streams corresponding to requests
