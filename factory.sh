#!/bin/sh

if which java
then

  configuration=$1
  java -jar Factory.jar "$configuration"
else

  echo "No Java installation available. Please install Java and try again."
  exit 1
fi