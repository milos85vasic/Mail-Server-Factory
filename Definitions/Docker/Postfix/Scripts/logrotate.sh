#!/bin/sh

postfix logrotate
find /var/log/postfix -mtime +120 -exec rm -f {} \;

sleep 604800; sh /logrotate.sh &

