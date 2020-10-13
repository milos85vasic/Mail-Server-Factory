#!/bin/sh

logs=/var/log
logFile=rspamd.log
log="$logs/$logFile"

if test -e ${log}
then

  cp ${log} "${logs}/$(($(date +%s%N)/1000000))_$logFile"
  echo "Log initialized: $(date)" > ${log}
  find ${logs} -name "*_$logFile" -mtime +120 -exec rm -f {} \; >> ${log}
  echo "Beginning of log file:" >> ${log}
else

  echo "Log not yet available for archiving: $(date)" >> ${log}
fi

sleep 604800; sh /logrotate.sh &