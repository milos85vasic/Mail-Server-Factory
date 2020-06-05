#!/bin/sh

dbPort=5432
echo "Starting Postfix on `hostname`" > /var/log/postfix.start.log
echo "Checking database port: $dbPort" >> /var/log/postfix.start.log
if echo "^C" | telnet postgres_database ${dbPort} | grep "Connected"
then
    echo "Process is available at port: $dbPort" >> /var/log/postfix.start.log

    postfix set-permissions >> /var/log/postfix.start.log
    postfix check >> /var/log/postfix.start.log
    postfix start >> /var/log/postfix.start.log
    if postfix status >> /var/log/postfix.start.log
    then

        ports=(465 587)
        for port in ${ports[@]}; do
            if echo "^C" | telnet 127.0.0.1 ${port} | grep "Connected"
            then
                echo "Postfix is listening at port: $port" >> /var/log/postfix.start.log
            else
                echo "Postfix is not bound to port: $port"
                exit 1
            fi
        done

        tail -f /var/log/postfix.start.log
    else
        exit 1
    fi
else
   echo "No process bound to port: $dbPort"
   exit 1
fi
