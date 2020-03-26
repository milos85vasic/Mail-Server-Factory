#!/bin/sh

declare -a pythons=("python" "python3" "python2")

for i in "${pythons[@]}"
do
   if hash "$i" 2>/dev/null; then
        echo "$i"
        exit 0
    fi
done

echo "python"

