# Demo

mitmproxy: man-in-the-middle proxy

Start [here](https://docs.mitmproxy.org/stable/howto-transparent/).

Demo is going to use local cluster, therefore mitmproxy and traffic have both soure and destination on the same machine (albeit the destination is in a k8s cluster running on local docker containers). This is different from networked setup. However, there is a new mode added to mitmproxy - so new it isn't even documented, but will be, in a few month - local mode - which allows transparent proxy-ing locally.

- start mitmproxy:

        mitmproxy --mode local:curl --showhost --ssl-insecure --mode transparent

- `--mode: local:curl` instructs mitmproxy to capture local traffic when it originates from within a `curl` process 
	- you can add it multiple times, to capture traffic originating in multiple processes
- `showhost` tells mitmproxy to use the `Host` header to construct URLs for display 
	- you can for example do a curl like:
	
		curl -H 'example.com' https://localhost:8080/some/endpoint

and the URL that mitmproxy will show will be https://example.com:8080/some/endpoint.
- `--ssl-insecure` tells mitmproxy to accept unverifiable certificates from the server to which it proxies requests
- `--mode transparent` tells mitmproxy to proxy at the network layer, rather than the application layer, i.e. other than decrypting client requests and re-encrypting them it won't change anything in them
	- this is important when you use mitmproxy in a networked setup - you don't want requests to be addressed to mitmproxy itself, since mitmproxy wouldn't know where to forward them

- now do your requests, inspect the traffic and draw your conclusions.

        curl -k -vvv https://localhost/name-builder/name


