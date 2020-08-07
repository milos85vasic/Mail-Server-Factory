#!/bin/sh

logFile="rspamd.start.log"
echo "Rspamd START: $(date)" > ${logFile}
workerController=/etc/rspamd/local.d/worker-controller.inc
# FIXME: Variable merging issue!
# webuiPassword=__SERVICE.ANTI_SPAM.WEBUI.PASSWORD__
webuiPassword=Test12345
echo "password = \"$(rspamadm pw --encrypt -p $webuiPassword)\"" > ${workerController}
rspamd -u _rspamd -g _rspamd
tail -F ${logFile}