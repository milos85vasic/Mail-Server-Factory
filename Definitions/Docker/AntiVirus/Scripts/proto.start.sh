#!/bin/sh

touch /var/log/clamd.scan
chgrp clamscan /var/log/clamd.scan
chown clamscan /var/log/clamd.scan
chmod 600 /var/log/clamd.scan

chown clamscan /etc/clamd.d/scan.conf
chgrp clamscan /etc/clamd.d/scan.conf
chmod 600 /etc/clamd.d/scan.conf

antivirusStackLog=/var/log/antivirus.stack.log
echo "Antivirus stack starting: `date`" > ${antivirusStackLog}
chmod 600 ${antivirusStackLog}
sh /do_clam.sh ${antivirusStackLog} &

amavisLog=/var/log/amavis.log
touch ${amavisLog}
chown amavis ${amavisLog}
chmod 600 ${amavisLog}

if amavisd >> ${antivirusStackLog}
then

    ports=({{SERVICE.ANTI_VIRUS.PORTS.PORT}})
    for port in ${ports[@]}; do
        if echo "^C" | telnet 127.0.0.1 "${port}" | grep "Connected"
        then
            echo "Amavis is listening on port: $port" >> ${antivirusStackLog}
        else
            echo "Amavis is not bound to port: $port" >> ${antivirusStackLog}
            exit 1
        fi
    done

    sh /logrotate.sh &
    tail -F ${antivirusStackLog}
else

    echo "Amavis not started" >> ${antivirusStackLog}
fi