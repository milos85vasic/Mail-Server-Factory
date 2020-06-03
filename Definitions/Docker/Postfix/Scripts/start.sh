#!/bin/sh

SERVICE_PORT=5432
echo "Starting Postfix on `hostname`" > /var/log/postfix.start.log
echo "Checking database port: $SERVICE_PORT" >> /var/log/postfix.start.log
if nc -zv postgres_database ${SERVICE_PORT} | grep "Connected"
then
    echo "Process is available at port: $SERVICE_PORT" >> /var/log/postfix.start.log
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
