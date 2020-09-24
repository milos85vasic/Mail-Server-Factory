#!/bin/sh

if which java; then

  factoryType=$1
  configuration=$2
  factoryPath="/usr/local/bin"

  jarFile="$factoryPath/factory_$factoryType.jar"
  if test -e "$jarFile"; then

    java -jar "$jarFile" "$configuration"
  else

    echo "No $factoryType factory jar found at: $factoryPath"
    exit 1
  fi
else

  echo "No Java installation available. Please install Java and try again."
  exit 1
fi
