#!/bin/sh

logFile="rspamd.start.log"
echo "Rspamd START OK" > ${logFile}
tail -F ${logFile}