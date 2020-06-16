#!/bin/sh

PERCENT=$1
USER=$2

cat <<EOF | /usr/lib/dovecot/dovecot-lda -d ${USER} -o plugin/quota=maildir
    From: {{SERVER.POSTMASTER}}
    Subject: Quota warning
    Your mailbox is currently ${PERCENT}% full.
EOF