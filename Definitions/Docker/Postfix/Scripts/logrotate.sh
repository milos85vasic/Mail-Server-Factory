#!/bin/sh

postfix logrotate
# find /var/log/postfix -mtime +120 -exec rm -f {} \;
find /var/log/postfix -mmin +5 -exec rm -f {} \;

# sleep 604800; sh /logrotate.sh &
sleep 60; sh /logrotate.sh &
