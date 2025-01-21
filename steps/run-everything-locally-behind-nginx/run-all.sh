#!/bin/bash

set -x

./run-adjectives.sh >adjectives.log 2>&1 &
ADJECTIVES_PID=$!

./run-nouns.sh >nouns.log 2>&1 &
NOUNS_PID=$!

./run-number-pairs.sh >number-pairs.log 2>&1 &
NUMBER_PAIRS_PID=$!

./run-names.sh >names.log 2>&1 &
NAMES_PID=$!

./run-nginx.sh &

# apps take 5s to start when started individually
sleep 10

echo "All applications plus the TLS gateway are started. Press ENTER to stop everything."
read WHATEVER

sudo nginx -s stop

# attempt to stop apps in a civilized way
kill -SIGTERM ${ADJECTIVES_PID} &
kill -SIGTERM ${NOUNS_PID} &
kill -SIGTERM ${NUMBER_PAIRS_PID} &
kill -SIGTERM ${NAMES_PID} &

# give apps 10s to stop
sleep 5

# if an app failed to end, kill it forcefully, but avoid ugly output like "no such process"
kill -SIGKILL ${ADJECTIVES_PID} > /dev/null 2>&1 &
kill -SIGKILL ${NOUNS_PID} > /dev/null 2>&1 &
kill -SIGKILL ${NUMBER_PAIRS_PID} > /dev/null 2>&1 &
kill -SIGKILL ${NAMES_PID} > /dev/null 2>&1 &
