#!/bin/sh

logFile="rspamd.start.log"
echo "Rspamd START: $(date)" > ${logFile}
rspamadm pw --encrypt -p {{SERVICE.ANTI_SPAM.WEBUI.PASSWORD}} >> ${logFile}
tail -F ${logFile}