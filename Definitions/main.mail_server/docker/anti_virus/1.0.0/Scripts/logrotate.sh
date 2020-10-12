#!/bin/sh

logs=/var/log
logFile=amavis.log
amavisLog="${logs}/${logFile}"

if test -e ${amavisLog}
then

  cp ${amavisLog} "${logs}/$(($(date +%s%N)/1000000))_$logFile"
  echo "Log initialized: $(date)" > ${amavisLog}
  find ${logs} -name "*_$logFile" -mtime +120 -exec rm -f {} \; >> ${amavisLog}
  echo "Beginning of log file:" >> ${amavisLog}
else

  echo "Log not yet available for archiving: $(date)" >> ${amavisLog}
fi

sleep 604800; sh /logrotate.sh &