#!/bin/sh

if which java; then

  factoryType=$1
  configuration=$2
  if test -e Factory.jar; then

    java -jar "Factory/$factoryType/Factory.jar" "$configuration"
  else

    echo "No factory jar found at: $(pwd)/Factory/$factoryType"
    exit 1
  fi
else

  echo "No Java installation available. Please install Java and try again."
  exit 1
fi
