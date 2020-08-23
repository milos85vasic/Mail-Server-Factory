#!/bin/sh

logFile="rspamd.start.log"
echo "Rspamd START: $(date)" > ${logFile}
sh /logrotate.sh &

chown -R _rspamd /var/run/rspamd
chgrp -R _rspamd /var/run/rspamd

chown -R _rspamd /var/lib/rspamd
chgrp -R _rspamd /var/lib/rspamd

if rspamd -u _rspamd -g _rspamd
then

  tail -F ${logFile}
else

  echo "Rspamd not started" >> ${logFile}
  exit 1
fi

