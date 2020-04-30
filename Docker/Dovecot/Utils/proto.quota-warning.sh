#!/bin/sh

PERCENT=$1
USER=$2

# TODO: Replace postmaster@example.com with  __PROPER_MAIL__
cat <<EOF | /usr/lib/dovecot/dovecot-lda -d ${USER} -o plugin/quota=maildir
    From: postmaster@example.com
    Subject: Quota warning
    Your mailbox is currently ${PERCENT}% full.
EOF