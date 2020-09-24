#!/bin/sh

factoryScript=Factory/Mail/factory.sh
if test -e "$factoryScript"; then

  $factoryScript "Mail" "$1"
else

  echo "No factory.sh found at: $(pwd)/$factoryScript"
fi