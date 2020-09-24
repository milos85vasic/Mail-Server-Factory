#!/bin/sh

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

  sudo cp -f Factory/Release/Factory.jar /usr/local/bin &&
    cp -f factory.sh /usr/local/bin
else

  echo "No Factory.jar found"
  exit 1
fi
