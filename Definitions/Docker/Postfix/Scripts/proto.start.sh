#!/bin/sh

dovecotSaslPort=12345
dovecotLmtpPort=12346
dbPort={{SERVICE.DATABASE.PORTS.PORT}}
antivirusPort={{SERVICE.ANTI_VIRUS.PORTS.PORT}}

postfixLog=/var/log/postfix.start.log
echo "Starting Postfix" > ${postfixLog}

echo "Checking database port: $dbPort" >> ${postfixLog}
if echo "^C" | telnet {{SERVICE.DATABASE.NAME}} ${dbPort} | grep "Connected"
then
    echo "Database process is bound to port: $dbPort" >> ${postfixLog}
else
   echo "No process bound to port: $dbPort" >> ${postfixLog}
   exit 1
fi

echo "Checking AntiVirus scanner service port: $antivirusPort" >> ${postfixLog}
if echo "^C" | telnet {{SERVICE.ANTI_VIRUS.NAME}} ${antivirusPort} | grep "Connected"
then
    echo "AntiVirus scanner service is bound to port: $antivirusPort" >> ${postfixLog}
else
   echo "No AntiVirus scanner service bound to port: $antivirusPort" >> ${postfixLog}
   exit 1
fi

echo "Checking Dovecot SASL port: $dovecotSaslPort" >> ${postfixLog}
if echo "^C" | telnet {{SERVICE.MAIL_RECEIVE.NAME}} ${dovecotSaslPort} | grep "Connected"
then
    echo "Dovecot process is bound to port: $dovecotSaslPort" >> ${postfixLog}
else
   echo "No process bound to port: $dovecotSaslPort" >> ${postfixLog}
   exit 1
fi

echo "Checking Dovecot LMTP port: $dovecotLmtpPort" >> ${postfixLog}
if echo "^C" | telnet {{SERVICE.MAIL_RECEIVE.NAME}} ${dovecotLmtpPort} | grep "Connected"
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

    ports=(465 587 {{SERVICE.MAIL_SEND.PORTS.PORT_ANTI_VIRUS}})
    for port in ${ports[@]}; do
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
