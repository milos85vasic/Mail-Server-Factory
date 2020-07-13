#!/bin/sh

chgrp -R clamupdate /var/lib/clamav
chown -R clamupdate /var/lib/clamav
chmod 700 /var/lib/clamav

clamLog=$1
freshclam >> ${clamLog}
clamscan --infected --remove --recursive /target >> ${clamLog}

sleep 43200; sh /do_clam.sh &