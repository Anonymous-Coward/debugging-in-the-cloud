# Demo

Java agent [here](https://github.com/jsslkeylog/jsslkeylog)

Might need to recompile from sources to make it work - jar in release is sometimes not working with every Java version. That's simple, however - simple maven project with no exotic dependencies or settings.

To capture SSL session keys you need something that is accessed via TLS. For this, we'll modify the cluster deployment a little:
- make the adjectives service accessible from outside - via the nginx ingress gateway, which only listens on the https port
- modify the name builder to call the adjectives service via its cluster-external URL, i.e via https over nginx

Without a trusted certificate authority to generate a certificate for nginx, the http client inside the name builder will fail the calls for adjectives over https. Therefore, disable certificate validation - show the code, just briefly.

Reason for not constructing a certificate authority that should be added to the trust store of the name builder: too complicated, would obscure the point of this presentation. Coarse outline:
- generate a self-signed root CA certificate
- generate an intermediate signing certificate signed with the root CA certificate
- generate a leaf certificate signed with the intermediate certificate, adding localhost and the local machine's IP address in it, maybe also the local machine's fully qualified domain name
- add leaf certificate to a a Java trust store
- modify image for name builder to include that trust store and Java inside the image to use that trust store by default (or modify the command to specify the strust store)
=> way too complicated for this demo

So:

- add an nginx ingress resource for the adjectives service - execute `kubectl apply` with a file with these contents:

        apiVersion: networking.k8s.io/v1
        kind: Ingress
        metadata:
          name: nginx-ingress
          namespace: adjectives
        spec:
          rules:
          - http:
              paths:
              - pathType: Prefix
                path: "/adjectives"
                backend:
                  service:
                    name: word
                    port:
                      number: 80

- find the local non-loopback IP address 
	- on linux: `ip addr list` then copying the IP number for the first `en...` adapter - or the one starting with `wl...` if you're only connected via a wireless adapter, with no ethernet cabled connection

- edit the name builder deployment and replace:

            - name: ADJECTIVES_URL
              value: http://word.adjectives.svc.cluster.local/adjectives

with:

            - name: ADJECTIVES_URL
              value: https://<local non-loopback IP address>/adjectives

- put the jar file (`jSSLKeyLog.jar`) in the same directory as your application jar

- modify Java command line of names builder by adding this as the first parameter to `java`:

        -javaagent:jSSLKeyLog.jar=/tmp/tls.keys

Why `/tmp/tls.keys`: the app of interest inside in a container usually doesn't run as root, and as such doesn't have write access in too many places by default. Typically, however, `/tmp` is world writable.

- now run the application - in case of our cluster, rebuild and redeploy the cluster, so the docker image gets updated
	- make sur the Dockerfile copies the `jSSLKeyLog.jar` into the image

- capture traffic using ksniff as described in step 03, but this time dump the output into a local file:

        POD_NAME=$(kubectl -n name-builder get pods | grep name-builder | awk '{print $1}')
        kubectl sniff ${POD_NAME} -n name-builder -f 'tcp port 8080' -p -o ./pcakets.pcap

- once you have executed your requests, use `kubectl cp` to copy the file out of the container:

        kubectl cp -n name-builder <pod name>:/tmp/tls.keys ./tls.keys

- now stop ksniff and manually delete the sniffer pod - there is a fix for ksniff not removing the sniffer container when ended with Ctrl+C, but it's not yet released.

        POD_NAME=$(kubectl -n name-builder get pods | grep sniff | awk '{print $1}')
        kubectl -n name-builder delete pod ${POD_NAME}

- start wireshark, navigate through to Edit -> Settings -> Protocols -> TLS, select the file `./tls.keys` into the field labeled `(Pre)-Master-Secret log filename`

- open the file `./pcakets.pcap`

- show http streams embedded in TLS streams as cleartext
