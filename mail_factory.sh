#!/bin/sh

script="factory_mail.sh"
factoryPath="/usr/local/bin"

if test -e "$factoryPath/$script"; then

  "$factoryPath/$script" "mail" "$1"
else

  echo "No $script found at: $factoryPath"
  exit 1
fi
