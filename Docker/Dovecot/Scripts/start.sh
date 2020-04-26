#!/bin/sh

echo "Starting Dovecot on `hostname`"

chmod -R +r /run/dovecot
chmod -R +w /run/dovecot

rsyslogd
dovecot
# TODO: Re-enable:
# tail -f /var/log/maillog