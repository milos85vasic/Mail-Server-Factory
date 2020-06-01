#!/bin/sh

echo "Starting Postfix on `hostname`" > /var/log/postfix.start.log
# newaliases
postfix set-permissions >> /var/log/postfix.start.log
postfix check >> /var/log/postfix.start.log
postfix start >> /var/log/postfix.start.log
postfix status >> /var/log/postfix.start.log
tail -f /var/log/postfix.start.log