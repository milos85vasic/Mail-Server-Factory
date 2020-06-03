#!/bin/sh

echo "Starting Postfix on `hostname`" > /var/log/postfix.start.log
echo "Checking database port: {{DB.DB_PORT}}" >> /var/log/postfix.start.log
if ss -tulpn | grep ":{{DB.DB_PORT}}"
then
    echo "Process is available at port: {{DB.DB_PORT}}" >> /var/log/postfix.start.log
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
   echo "No process bound to port: {{DB.DB_PORT}}"
   exit 1
fi
