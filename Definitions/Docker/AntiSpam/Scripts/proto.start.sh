#!/bin/sh

logFile="/var/log/rspamd.start.log"
echo "Rspamd OK" > ${logFile}
tail ${logFile}