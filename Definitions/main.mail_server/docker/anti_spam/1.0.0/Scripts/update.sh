#!/bin/sh

memDb=$1
memDbPort=$2
memDbIp=$(sh /getip.sh "${memDb}")

echo "Redis configuration: ${memDb}:${memDbPort}"
echo "Updating Rspamd definitions: $(date)"
echo "Redis configuration [2]: ${memDb}: ip= ${memDbIp}, port= ${memDbPort}"
rm -f /var/lib/rspamd/*sqlite
wget -P /var/lib/rspamd https://rspamd.com/rspamd_statistics/bayes.ham.sqlite && \
wget -P /var/lib/rspamd https://rspamd.com/rspamd_statistics/bayes.spam.sqlite && \
chown _rspamd._rspamd /var/lib/rspamd/*sqlite && \
rspamadm statconvert --spam-db /var/lib/rspamd/bayes.spam.sqlite --symbol-spam BAYES_SPAM \
--ham-db /var/lib/rspamd/bayes.ham.sqlite --symbol-ham BAYES_HAM -h \
"${memDbIp}:${memDbPort}"

sleep 43200; sh /update.sh "${memDb}" "${memDbPort}" &


