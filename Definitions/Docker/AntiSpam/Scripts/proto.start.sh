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

  ports=({{SERVICE.ANTI_SPAM.PORTS.PROXY}} {{SERVICE.ANTI_SPAM.PORTS.WORKER}} {{SERVICE.ANTI_SPAM.PORTS.WEBUI}})
  for port in ${ports[@]}; do
      if echo "^C" | telnet 127.0.0.1 "${port}" | grep "Connected"
      then
          echo "Rspamd is listening on port: $port" >> ${logFile}
      else
          echo "Rspamd is not bound to port: $port" >> ${logFile}
          exit 1
      fi
  done

  tail -F ${logFile}
else

  echo "Rspamd not started" >> ${logFile}
  exit 1
fi

