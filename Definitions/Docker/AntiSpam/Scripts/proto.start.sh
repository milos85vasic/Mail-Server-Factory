#!/bin/sh

logFile="rspamd.start.log"
echo "Rspamd START: $(date)" > ${logFile}
workerController=/etc/rspamd/local.d/worker-controller.inc
webuiPassword={{SERVICE.ANTI_SPAM.WEBUI.PASSWORD}}
echo "password = \"$(rspamadm pw --encrypt -p $webuiPassword)\";" > ${workerController}
sh /logrotate.sh &

chown -R _rspamd /var/run/rspamd
chgrp -R _rspamd /var/run/rspamd

if rspamd -u _rspamd -g _rspamd
then

  tail -F ${logFile}
else

  echo "Rspamd not started" >> ${logFile}
  exit 1
fi

