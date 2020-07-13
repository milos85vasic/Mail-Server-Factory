#!/bin/sh

touch /var/log/clamd.scan
chgrp clamscan /var/log/clamd.scan
chown clamscan /var/log/clamd.scan
chgrp clamscan /etc/clamd.d/scan.conf

antivirusStackLog=/var/log/antivirus.stack.log
echo "Antivirus stack starting: `date`" > ${antivirusStackLog}
sh /do_clam.sh ${antivirusStackLog} &

amavisLog=/var/log/amavis.log
touch ${amavisLog}
chown amavis ${amavisLog}
chmod 600 ${amavisLog}

if amavisd >> ${antivirusStackLog}
then

    tail -F ${antivirusStackLog}
else

    echo "Amavis not started" >> ${antivirusStackLog}
fi