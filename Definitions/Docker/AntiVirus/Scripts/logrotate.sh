#!/bin/sh

logs=/var/log
logFile=amavis.log
amavisLog="${logs}/${logFile}"

cp ${amavisLog} "${logs}/$(($(date +%s%N)/1000000))_$logFile"
echo "Log initialized: `date`" > ${amavisLog}

find ${logs} -name "*_$logFile" -mtime +120 -exec rm -f {} \; >> ${amavisLog}
echo "Beginning of log file:" >> ${amavisLog}

sleep 604800; sh /logrotate.sh &