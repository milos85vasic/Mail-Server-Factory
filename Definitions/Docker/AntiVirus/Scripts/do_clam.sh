#!/bin/sh

freshclam
clamscan --infected --remove --recursive /target