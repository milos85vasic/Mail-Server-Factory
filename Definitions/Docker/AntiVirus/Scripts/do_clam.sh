#!/bin/sh

chgrp -R clamupdate /var/lib/clamav
chown -R clamupdate /var/lib/clamav
chmod 750 /var/lib/clamav

clamLog=/var/log/clamd.run.log
echo "===== ClamAV iteration: `date` ==========" >> ${clamLog}
freshclam >> ${clamLog}
clamscan --infected --remove --recursive /target >> ${clamLog}

# sleep 43200; sh /do_clam.sh &
sleep 120; sh /do_clam.sh &