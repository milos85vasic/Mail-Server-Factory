#!/bin/sh

MOCKS="Mocks"
MAIL_SERVER_FACTORY="Mail-Server-Factory"

rm -rf "~/$MAIL_SERVER_FACTORY"
mkdir "~/$MAIL_SERVER_FACTORY"
mkdir "~/$MAIL_SERVER_FACTORY/$MOCKS"
cp Factory/mocks/* "~/$MAIL_SERVER_FACTORY/$MOCKS"