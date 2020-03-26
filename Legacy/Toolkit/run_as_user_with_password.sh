#!/bin/sh

su - "$1" <<! >/dev/null 2>&1
"$2"
$3 > /dev/tty
!