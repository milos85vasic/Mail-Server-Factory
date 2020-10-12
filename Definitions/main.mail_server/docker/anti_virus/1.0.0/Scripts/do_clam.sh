#!/bin/sh

chgrp -R clamupdate /var/lib/clamav
chown -R clamscan /var/lib/clamav
chmod 770 /var/lib/clamav

clamLog=$1
freshclam >> ${clamLog}
clamd >> ${clamLog}
clamscan --infected --remove --recursive /target >> ${clamLog}

sleep 43200; sh /do_clam.sh &