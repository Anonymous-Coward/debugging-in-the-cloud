# Local setup

Run all apps behind an nginx gateway to allow for local debugging with TLS.

Scripts:
- rebuild-all.sh: rebuild all apps, copy them to this directory
	- this one takes parameters and passes them to the maven builds - allowing you to skip tests for a faster refresh of jars
	- parameter to skip tests, for gradle, is `-x test`
- all run-*.sh except run-nginx.sh and run-all.sh: run one of each of the applications, with settings so that everything works locally
- run-nginx.sh: run nginx locally to have a TLS ingress
- run-all.sh: run nginx plus all applications
- rebuild-run-all.sh: first do the rebuild, then do run-all
	- this also takes parameters, and passes them to rebuild-all.sh

### Enable remote debugging

Java provides a remote debugging facility, via the [Java Debug Wire Protocol](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/introclientissues005.html). This facility is disabled by default. To enable it, add:

    -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=11111

to the Java command line. This will open up port 11111 inside the JVM, to which you can then
connect with your favorite remote Java debugger.

If you look into the documentation, using `agentlib` isn't documented there. It's used in this setup
to allow both remote debugging and capturing TLS keys via a java agent.

## Demo

- go through the scripts, start cluster, do some calls
- show that the apps work
- show how to attach remote debugger
