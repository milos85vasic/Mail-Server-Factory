#!/bin/sh

factoryType=$1
if test -e build.gradle && test -e Factory; then

  if which gradle; then
    if test -e gradlew; then

      echo "Gradle wrapper is available"
    else

      gradle wrapper
    fi

    ./gradlew clean && ./gradlew install
  fi
fi

if test Factory/Release/Application.jar; then

  factoryPath="/usr/local/bin"
  sudo cp -f Factory/Release/Application.jar "$factoryPath/factory_$factoryType.jar" &&
    cp -f factory.sh "$factoryPath/factory_$factoryType.sh"
else

  echo "No Application.jar found"
  exit 1
fi
