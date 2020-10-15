#!/bin/sh

script="factory_mail.sh"
factoryPath="$(sh mail_factory_path.sh)"

if test -e "$factoryPath/$script"; then

  "$factoryPath/$script" "mail" "$1"
else

  echo "No $script found at: $factoryPath"
  exit 1
fi
