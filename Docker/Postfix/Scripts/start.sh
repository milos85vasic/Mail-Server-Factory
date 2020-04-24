#!/bin/sh

# adding IP of a host to /etc/hosts
export HOST_IP=$(/sbin/ip route|awk '/default/ { print $3 }')
echo "$HOST_IP dockerhost" >> /etc/hosts

echo "Starting Postfix on `hostname` (IP: $HOST_IP)"


# TODO: Tbd.

rsyslogd
postfix start && postfix check
tail -f /var/log/mail.log