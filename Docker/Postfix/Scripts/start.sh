#!/bin/sh

echo "Starting Postfix on `hostname`"

# TODO: Tbd.

postfix start && postfix check
# TODO: tail -f /var/log/maillog