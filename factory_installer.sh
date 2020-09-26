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

if test Application/Release/Application.jar; then

  factoryPath="/usr/local/bin"
  echo "Please enter your sudo password to install the software" &&
    if sudo cp -f Application/Release/Application.jar "$factoryPath/factory_$factoryType.jar" &&
      cp -f factory.sh "$factoryPath/factory_$factoryType.sh"; then

      echo "Software has been installed with success"
    else

      echo "Software installation failed"
      exit 1
    fi
else

  echo "No Application.jar found"
  exit 1
fi
