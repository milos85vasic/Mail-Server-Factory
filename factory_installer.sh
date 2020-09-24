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

if test Factory/Release/Factory.jar; then

  factoryPath="/usr/local/bin/Factory/$factoryType"
  sudo mkdir -p "$factoryPath" &&
    cp -f Factory/Release/Factory.jar "$factoryPath" &&
    cp -f factory.sh "$factoryPath"
else

  echo "No Factory.jar found"
  exit 1
fi
