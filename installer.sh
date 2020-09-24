#!/bin/sh

installerScript=factory_installer.sh
if test -e "$installerScript"; then

  if ./$installerScript "Mail"; then

    factoryPath="/usr/local/bin"
    sudo cp -f factory.sh "$factoryPath"
  else

    echo "Installation failed"
    exit 1
  fi
else

  echo "No $installerScript found at: $(pwd)"
  exit 1
fi
