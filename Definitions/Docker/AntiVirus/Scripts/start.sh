#!/bin/sh

RUN touch /var/log/clamd.scan
RUN chgrp clamscan /var/log/clamd.scan
RUN chown clamscan /var/log/clamd.scan
RUN chgrp clamscan /etc/clamd.d/scan.conf

sh /do_clam.sh
# sleep 43200; sh /do_clam.sh &
sleep 120; sh /do_clam.sh &

tail -f /var/log/clamd.scan