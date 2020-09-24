#!/bin/sh

if test -e build.gradle && test -e Factory
then

  ./gradlew install
fi

if test Factory/Release/Factory.jar
then

  sudo cp -f Factory/Release/Factory.jar /usr/bin/ && \
  cp -f factory.sh /usr/bin/
else

  echo "No Factory.jar found"
  exit 1
fi