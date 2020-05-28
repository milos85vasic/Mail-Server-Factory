#!/bin/sh

echo "Starting Postfix on `hostname`" > /var/log/postfix.log

postfix start && postfix check > /var/log/postfix.log
tail -f /var/log/postfix.log