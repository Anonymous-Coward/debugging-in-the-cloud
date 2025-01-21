# Demo

- show `man socat`

- make socat image available in kind cluster:

        docker pull alpine/socat:latest
        kind load docker-image -n demo "alpine/socat:latest"

- edit name builder deployment inline and insert socat proxy:

        spec:
          template:
            spec:
              containers:
              - name: socat-proxy
                args:
                - -c
                - socat -v -d TCP-LISTEN:5080,bind=0.0.0.0,fork TCP:localhost:8080
                command:
                - sh
                image: alpine/socat:latest
                imagePullPolicy: Never
                ports:
                - containerPort: 5080
                  name: phttp
                  protocol: TCP
                resources: {}
                terminationMessagePath: /dev/termination-log
                terminationMessagePolicy: File

- wait for pod to restart

- tmux, split vertically, show socat log in right pane, execute request in left pane:

        curl -k -vvv https://localhost/name-builder/name
