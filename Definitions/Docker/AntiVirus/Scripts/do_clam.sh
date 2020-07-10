#!/bin/sh

chgrp -R clamupdate /var/lib/clamav
chown -R clamupdate /var/lib/clamav
chmod 750 /var/lib/clamav

freshclam
clamscan --infected --remove --recursive /target