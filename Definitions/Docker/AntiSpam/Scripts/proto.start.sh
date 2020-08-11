#!/bin/sh

logFile="rspamd.start.log"
echo "Rspamd START: $(date)" >${logFile}
workerController=/etc/rspamd/local.d/worker-controller.inc
webuiPassword={{SERVICE.ANTI_SPAM.WEBUI.PASSWORD}}
echo "password = \"$(rspamadm pw --encrypt -p $webuiPassword)\"" >${workerController}
rspamd -u _rspamd -g _rspamd
tail -F ${logFile}
