#!/bin/sh

echo "Starting Postfix on `hostname`"

rsyslogd
postfix start && postfix check
tail -f /var/log/maillog