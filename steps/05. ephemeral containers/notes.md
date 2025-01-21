# Demo

- show docs page: https://kubernetes.io/docs/concepts/workloads/pods/ephemeral-containers/
	- also says from what version

- tmux two vertical panes

- execute ephemeral container using alpine against node demo-worker in right pane

        k debug node/demo-worker --image=alpine -it -- sh

- docker exec into same pod in right pane

- show that `/host` is host's FS:
	- `chroot /host` in ephemeral container
	- touch a file in /tmp
	- touch another file in docker exec, also in /tmp
	- show that `ls /tmp` in both containers show the same thing

- say that this demo is only possible with a kubernetes you fully control - can't docker exec into host OS on azure, for example (but, if properly set up, you could ssh into VM running node - but that's not in scope here)
