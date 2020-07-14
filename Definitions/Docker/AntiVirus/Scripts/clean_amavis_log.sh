#!/bin/sh

amavisLog=$1

if test "`find ${amavisLog} -mtime +7`"
then
    echo "Log initialized: `date`" > ${amavisLog}

else
    echo "Log file is not old enough to be rotated: `date`" >> ${amavisLog}
fi

sleep 604800; sh /logrotate.sh ${amavisLog} &
