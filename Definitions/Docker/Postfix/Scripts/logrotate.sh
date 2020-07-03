#!/bin/sh

if test "`find /var/log/postfix/postfix.log -mmin +5`"
# if test "`find /var/log/postfix/postfix.log -mtime +7`"
then

    echo "Rotating log file"
    postfix logrotate
else

    echo "Log file is not old enough to be rotated"
fi
find /var/log/postfix -mmin +20 -exec rm -f {} \;
# find /var/log/postfix -mtime +120 -exec rm -f {} \;

sleep 5; sh /logrotate.sh
# sleep 604800; sh /logrotate.sh
