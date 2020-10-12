#!/bin/sh

PERCENT=$1
USER=$2

echo "From: {{SERVER.POSTMASTER}}
Subject: Quota warning

Your mailbox is currently $PERCENT% full.
" | /usr/libexec/dovecot/dovecot-lda -d "${USER}" -o plugin/quota=maildir