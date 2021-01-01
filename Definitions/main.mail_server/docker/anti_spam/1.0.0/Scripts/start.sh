#!/bin/sh

logFile="rspamd.start.log"
echo "Rspamd START: $(date)" > "${logFile}"
sh /logrotate.sh &

chown -R _rspamd /var/run/rspamd
chgrp -R _rspamd /var/run/rspamd

if test -e /var/lib/rspamd/dkim/; then

  echo "$(date) DKIM directory exists" >> "${logFile}"
else

  echo "$(date) Initializing DKIM directory" >> "${logFile}"
  mkdir -p /var/lib/rspamd/dkim/
fi

if test -e /var/lib/rspamd/dkim/mail.key; then

  echo "$(date) DKIM keys are available" >> "${logFile}"
else

  echo "$(date) Initializing DKIM keys" >> "${logFile}"
  rspamadm dkim_keygen -b 2048 -s mail -k /var/lib/rspamd/dkim/mail.key | tee -a /var/lib/rspamd/dkim/mail.pub
fi

chown -R _rspamd /var/lib/rspamd
chgrp -R _rspamd /var/lib/rspamd
chmod 440 /var/lib/rspamd/dkim/*

if rspamd -u _rspamd -g _rspamd; then

  proxyPort=$1
  workerPort=$2
  webUiPort=$3
  webUiPassword=$4
  memDb=$5
  memDbPort=$6

  echo "PARAMETERS: proxyPort=${proxyPort}, workerPort=${workerPort}, webUiPort=${webUiPort}" >> "${logFile}"
  echo "PARAMETERS: webUiPassword=${webUiPassword}" >> "${logFile}"
  export IFS=";"
  ports="${proxyPort};${workerPort};${webUiPort}"
  for port in $ports; do

    if echo "^C" | telnet 127.0.0.1 "${port}" | grep "Connected"; then
      echo "Rspamd is listening on port: $port" >> "${logFile}"
    else
      echo "Rspamd is not bound to port: $port" >> "${logFile}"
      exit 1
    fi
  done

  rspamc stat -P "${webUiPassword}" >> "${logFile}"
  sleep 120; sh /update.sh "${memDb}" "${memDbPort}" &
  tail -F "${logFile}"
else

  echo "Rspamd not started" >> "${logFile}"
  exit 1
fi
