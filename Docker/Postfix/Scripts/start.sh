#!/bin/sh

echo "Starting Postfix on `hostname`"

systemctl start rsyslog
postfix start && postfix check
tail -f /var/log/maillog