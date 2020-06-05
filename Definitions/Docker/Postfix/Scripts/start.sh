#!/bin/sh

dbPort=5432
postfixLog=/var/log/postfix.start.log
echo "Starting Postfix on `hostname`" > ${postfixLog}
echo "Checking database port: $dbPort" >> ${postfixLog}
if echo "^C" | telnet postgres_database ${dbPort} | grep "Connected"
then
    echo "Process is available on port: $dbPort" >> ${postfixLog}

    postfix set-permissions >> ${postfixLog}
    postfix check >> ${postfixLog}
    postfix start >> ${postfixLog}
    if postfix status >> ${postfixLog}
    then

        ports=(465 587)
        for port in ${ports[@]}; do
            if echo "^C" | telnet 127.0.0.1 ${port} | grep "Connected"
            then
                echo "Postfix is listening on port: $port" >> ${postfixLog}
            else
                echo "Postfix is not bound to port: $port" >> ${postfixLog}
                exit 1
            fi
        done

        tail -f ${postfixLog}
    else
        exit 1
    fi
else
   echo "No process bound to port: $dbPort" >> ${postfixLog}
   exit 1
fi
