#!/bin/sh

echo "Starting Postfix on `hostname`"

# TODO: Tbd.

rsyslogd
postfix start && postfix check
tail -f /var/log/mail.log