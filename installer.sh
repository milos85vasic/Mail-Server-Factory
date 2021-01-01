#!/bin/sh

defaultFactoryPath="/usr/local/bin"
if [ -z "$1" ]
then

  factoryPath="$defaultFactoryPath"
else

  factoryPath="$1"
fi

mail_factory_full_path="$defaultFactoryPath/mail_factory_path.sh"
if test -e "$mail_factory_full_path"; then

  rm -f "$mail_factory_full_path"
fi

echo """
#!/bin/sh

echo $factoryPath
""" > "$mail_factory_full_path" && chmod 700 "$mail_factory_full_path"

installerScript=Core/Utils/factory_installer.sh
if test -e "$installerScript"; then

  if "$installerScript" "mail" "$factoryPath"; then

    sudo cp -f Core/Utils/factory.sh "$factoryPath" &&
      cp -f mail_factory "$factoryPath"
  else

    echo "Installation failed"
    exit 1
  fi
else

  echo "No $installerScript found at: $(pwd)"
  exit 1
fi
