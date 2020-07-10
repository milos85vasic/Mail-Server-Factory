#!/bin/sh

touch /var/log/clamd.scan
chgrp clamscan /var/log/clamd.scan
chown clamscan /var/log/clamd.scan
chgrp clamscan /etc/clamd.d/scan.conf

sh /do_clam.sh

tail -f /var/log/clamd.scan