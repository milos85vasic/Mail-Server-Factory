#!/bin/sh

logFile="rspamd.start.log"
echo "Rspamd START: $(date)" > ${logFile}
tail -F ${logFile}