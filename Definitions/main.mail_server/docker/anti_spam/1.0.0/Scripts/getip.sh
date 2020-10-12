#!/bin/sh

ADDRESS=$1
if nslookup "${ADDRESS}" | grep "can't find"
then

  echo "Could not obtain ip address for: $ADDRESS"
else

  nslookup "${ADDRESS}" | sed '/^[[:space:]]*$/d' | tail -1 | tr -d "[:blank:]" | sed s/Address://
fi