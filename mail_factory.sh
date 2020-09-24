#!/bin/sh

script="factory_mail.sh"
if test -e "$script"; then

  $script "mail" "$1"
else

  echo "No $script found at: $(pwd)"
  exit 1
fi
