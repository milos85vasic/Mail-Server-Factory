#!/bin/sh

installerScript=factory_installer.sh
if test -e "$installerScript"; then

  ./$installerScript "Mail"
else

  echo "No $installerScript found at: $(pwd)"
fi
