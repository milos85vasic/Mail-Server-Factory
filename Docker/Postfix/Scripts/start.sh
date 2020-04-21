#!/bin/sh

echo "Starting Postfix"

# TODO: Tbd.

service postfix start
chkconfig postfix on
service postfix status