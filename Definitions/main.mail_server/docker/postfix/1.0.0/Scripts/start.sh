#!/bin/sh

dbName=$3
avName=$4
dbPort=$1
avPort=$2
avPortMailSend=$6
serviceReceive=$5
dovecotSaslPort=12345
dovecotLmtpPort=12346

postfixLog=/var/log/postfix.start.log
echo "Starting Postfix" > ${postfixLog}

echo "Checking database port: $dbPort" >> ${postfixLog}
if echo "^C" | telnet "${dbName}" "${dbPort}" | grep "Connected"
then
    echo "Database process is bound to port: $dbPort" >> ${postfixLog}
else
   echo "No process bound to port: $dbPort" >> ${postfixLog}
   exit 1
fi

echo "Checking AntiVirus scanner service port: $avPort" >> ${postfixLog}
if echo "^C" | telnet "${avName}" "${avPort}" | grep "Connected"
then
    echo "AntiVirus scanner service is bound to port: $avPort" >> ${postfixLog}
else
   echo "No AntiVirus scanner service bound to port: $avPort" >> ${postfixLog}
   exit 1
fi

echo "Checking Dovecot SASL port: $dovecotSaslPort" >> ${postfixLog}
if echo "^C" | telnet "${serviceReceive}" ${dovecotSaslPort} | grep "Connected"
then
    echo "Dovecot process is bound to port: $dovecotSaslPort" >> ${postfixLog}
else
   echo "No process bound to port: $dovecotSaslPort" >> ${postfixLog}
   exit 1
fi

echo "Checking Dovecot LMTP port: $dovecotLmtpPort" >> ${postfixLog}
if echo "^C" | telnet "${serviceReceive}" ${dovecotLmtpPort} | grep "Connected"
then
    echo "Dovecot process is bound to port: $dovecotLmtpPort" >> ${postfixLog}
else
   echo "No process bound to port: $dovecotLmtpPort" >> ${postfixLog}
   exit 1
fi

postfix set-permissions >> ${postfixLog}
newaliases
postfix check >> ${postfixLog}
postfix start >> ${postfixLog}
if postfix status >> ${postfixLog}
then

    export IFS=";"
    ports="465;587;${avPortMailSend}"
    for port in $ports; do

        if echo "^C" | telnet 127.0.0.1 "${port}" | grep "Connected"
        then
            echo "Postfix is listening on port: $port" >> ${postfixLog}
        else
            echo "Postfix is not bound to port: $port" >> ${postfixLog}
            exit 1
        fi
    done

    sh /logrotate.sh &
    tail -F ${postfixLog}
else
    exit 1
fi
