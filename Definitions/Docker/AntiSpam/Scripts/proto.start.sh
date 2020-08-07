#!/bin/sh

logFile="rspamd.start.log"
echo "Rspamd START: $(date)" > ${logFile}
workerController=/etc/rspamd/local.d/worker-controller.inc
echo "password = \"${rspamadm pw --encrypt -p {{SERVICE.ANTI_SPAM.WEBUI.PASSWORD}}}\"" > ${workerController}
tail -F ${logFile}