#!/bin/sh

logs=/var/log/dovecot
logFile=dovecot.log
infoLogFile=dovecot.info.log
debugLogFile=dovecot.debug.log

dovecotLog="$logs/$logFile"
dovecotInfoLog="$logs/$infoLogFile"
dovecotDebugLog="$logs/$debugLogFile"

if test -e ${dovecotLog}
then

  cp ${dovecotLog} "${logs}/$(($(date +%s%N)/1000000))_$logFile"
  echo "Log initialized: $(date)" > ${dovecotLog}
  echo "Beginning of log file:" >> ${dovecotLog}
fi

if test -e ${dovecotInfoLog}
then

  cp ${dovecotInfoLog} "${logs}/$(($(date +%s%N)/1000000))_$infoLogFile"
  echo "Log initialized: $(date)" > ${dovecotInfoLog}
  echo "Beginning of log file:" >> ${dovecotInfoLog}
fi

if test -e ${dovecotDebugLog}
then

  cp ${dovecotDebugLog} "${logs}/$(($(date +%s%N)/1000000))_$debugLogFile"
  echo "Log initialized: $(date)" > ${dovecotDebugLog}
  echo "Beginning of log file:" >> ${dovecotDebugLog}
fi

find ${logs} -name "*_$logFile" -mtime +120 -exec rm -f {} \; >> ${dovecotLog}
find ${logs} -name "*_$infoLogFile" -mtime +120 -exec rm -f {} \; >> ${dovecotInfoLog}
find ${logs} -name "*_$debugLogFile" -mtime +120 -exec rm -f {} \; >> ${dovecotDebugLog}

sleep 604800; sh /logrotate.sh &