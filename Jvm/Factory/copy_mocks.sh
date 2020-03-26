#!/bin/sh

echo "Copying mocks."
rm -rf Factory/build/libs/mocks
mkdir Factory/build/libs/mocks
cp Factory/mocks/* Factory/build/libs/mocks
echo "Mocks copying complete."
ls -lF Factory/build/libs/mocks