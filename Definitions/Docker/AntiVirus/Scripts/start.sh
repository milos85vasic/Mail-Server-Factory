#!/bin/sh

sh /do_clam.sh
# sleep 43200; sh /do_clam.sh &
sleep 120; sh /do_clam.sh &

tail -f /var/log/clamd.scan