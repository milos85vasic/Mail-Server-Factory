#!/bin/sh

postfixRotateLog=/var/log/postfix.rotate.log
if test "`find /var/log/postfix/postfix.log -mmin +2`"
then

    echo "Rotating log file" >> ${postfixRotateLog}
    postfix logrotate
else

    echo "Log file is not old enough to be rotated" >> ${postfixRotateLog}
fi
find /var/log/postfix -mmin +10 -exec rm -f {} \;

echo "Logs directory file list:" >> ${postfixRotateLog}
ls -lF >> ${postfixRotateLog}

sleep 5; sh /logrotate.sh &
