#!/bin/sh

dbPort={{SERVICE.DATABASE.PORTS.PORT}}
dovecotLog=/var/log/dovecot.start.log
echo "Starting Dovecot" > ${dovecotLog}

echo "Checking database port: $dbPort" >> ${dovecotLog}
if echo "^C" | telnet {{SERVICE.DATABASE.NAME}} ${dbPort} | grep "Connected"
then
    echo "Database process is bound to port: $dbPort" >> ${dovecotLog}
else
   echo "No process bound to port: $dbPort" >> ${dovecotLog}
   exit 1
fi

chmod -R +r /run/dovecot
chmod -R +w /run/dovecot
chown -R vmail ./maildir
chgrp -R vmail ./maildir
chgrp -R vmail /usr/local/vmail
chown -R vmail /usr/local/vmail
chgrp -R vmail /etc/dovecot/masters
chown -R vmail /etc/dovecot/masters

if dovecot >> ${dovecotLog}
then

    doveadm log errors >> ${dovecotLog}
    dovecot log errors >> ${dovecotLog}
    ports=(110 143 993 995 12345 12346 12347 4190 2000)
    for port in ${ports[@]}; do
        if echo "^C" | telnet 127.0.0.1 "${port}" | grep "Connected"
        then
            echo "Dovecot is listening on port: $port" >> ${dovecotLog}
        else
            echo "Dovecot is not bound to port: $port" >> ${dovecotLog}
            exit 1
        fi
    done

    sh /logrotate.sh &
    tail -F ${dovecotLog}
else
    exit 1
fi