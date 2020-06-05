#!/bin/sh

SERVICE_PORT=5432
echo "Starting Postfix on `hostname`" > /var/log/postfix.start.log
echo "Checking database port: $SERVICE_PORT" >> /var/log/postfix.start.log
if echo "^C" | telnet postgres_database ${SERVICE_PORT} | grep "Connected"
then
    echo "Process is available at port: $SERVICE_PORT" >> /var/log/postfix.start.log

    if echo "^C" | telnet 127.0.0.1 465 | grep "Connected"
    then
        echo "Postfix is listening ar port: 465" >> /var/log/postfix.start.log
    else
        echo "Postfix is not bound to port: 465"
        exit 1
    fi

    if echo "^C" | telnet 127.0.0.1 587 | grep "Connected"
    then
        echo "Postfix is listening ar port: 587" >> /var/log/postfix.start.log
    else
        echo "Postfix is not bound to port: 587"
        exit 1
    fi

    postfix set-permissions >> /var/log/postfix.start.log
    postfix check >> /var/log/postfix.start.log
    postfix start >> /var/log/postfix.start.log
    if postfix status >> /var/log/postfix.start.log
    then
        tail -f /var/log/postfix.start.log
    else
        exit 1
    fi
else
   echo "No process bound to port: $SERVICE_PORT"
   exit 1
fi
