#!/bin/sh

logs=/var/log
logFile=rspamd.log
rspamdLog="$logs/$logFile"

cp ${rspamdLog} "${logs}/$(($(date +%s%N)/1000000))_$logFile"
echo "Log initialized: $(date)" > ${rspamdLog}
find ${logs} -name "*_$logFile" -mtime +120 -exec rm -f {} \; >> ${rspamdLog}
echo "Beginning of log file:" >> ${rspamdLog}

sleep 604800; sh /logrotate.sh &