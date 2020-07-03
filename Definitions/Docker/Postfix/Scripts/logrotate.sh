#!/bin/sh

postfixRotateLog=/var/log/postfix.rotate.log

if test "`find /var/log/postfix/postfix.log -mtime +7`"
then
    echo "Rotating log file" >> ${postfixRotateLog}
    postfix logrotate

    find /var/log/postfix -mtime +120 -exec rm -f {} \;
    echo "Logs directory file list:" >> ${postfixRotateLog}
    ls -lF /var/log/postfix >> ${postfixRotateLog}
else
    echo "Log file is not old enough to be rotated" >> ${postfixRotateLog}
fi

sleep 604800; sh /logrotate.sh &
