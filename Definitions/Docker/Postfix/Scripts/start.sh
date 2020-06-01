#!/bin/sh

echo "Starting Postfix on `hostname`" >> /var/log/postfix.log
#echo "- - - - - - - - - - - - - - - " >> /var/log/postfix.log
#echo "Postfix configuration files:" >> /var/log/postfix.log
#ls /etc/postfix >> /var/log/postfix.log
#echo "- - - - - - - - - - - - - - - " >> /var/log/postfix.log
#echo "Postfix configuration:" >> /var/log/postfix.log
#cat /etc/postfix/main.cf >> /var/log/postfix.log
#echo "- - - - - - - - - - - - - - - " >> /var/log/postfix.log
#echo "Postfix certificates:" >> /var/log/postfix.log
#ls /certificates >> /var/log/postfix.log
#echo "- - - - - - - - - - - - - - - " >> /var/log/postfix.log
postfix start && postfix check #>> /var/log/postfix.log
#echo "Postfix started on `hostname`" >> /var/log/postfix.log
#echo "- - - - - - - - - - - - - - - " >> /var/log/postfix.log
tail -f /var/log/postfix.log