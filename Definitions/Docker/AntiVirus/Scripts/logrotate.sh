#!/bin/sh

amavisLog=$1
logsDir=/var/log
amavisRotateLog=/var/log/amavis.rotate.log

if test "`find ${amavisLog} -mtime +7`"
then
    echo "Rotating log file" > ${amavisRotateLog}
    mv ${amavisLog} "`date`_$amavisLog"
    touch ${amavisLog}
    chown amavis ${amavisLog}
    chmod 600 ${amavisLog}
    amavisd reload && amavisd

    find ${logsDir}/ -name "*_$amavisLog" -mtime +120 -exec rm -f {} \;
    echo "Logs directory file list:" >> ${amavisRotateLog}
    ls -lF ${logsDir} >> ${amavisRotateLog}
else
    echo "Log file is not old enough to be rotated" >> ${amavisRotateLog}
fi

sleep 604800; sh /logrotate.sh &
