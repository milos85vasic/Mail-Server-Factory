#!/bin/sh

touch /var/log/clamd.scan
chgrp clamscan /var/log/clamd.scan
chown clamscan /var/log/clamd.scan
chgrp clamscan /etc/clamd.d/scan.conf

clamLog=/var/log/clamd.run.log
echo "ClamAV starting: `date`" > ${clamLog}
sh /do_clam.sh ${clamLog} &

tail -F ${clamLog}