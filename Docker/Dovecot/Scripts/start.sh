#!/bin/sh

echo "Starting Dovecot on `hostname`"

mkdir /run/dovecot
chmod -R +r /run/dovecot
chmod -R +w /run/dovecot

rsyslogd
dovecot
tail -f /var/log/mail.log