#!/bin/sh

if test Factory/Release/Factory.jar
then

  cp -f Factory/Release/Factory.jar /usr/bin/
else

  echo "No Factory.jar found"
  exit 1
fi