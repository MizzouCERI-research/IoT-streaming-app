#!/bin/bash

if [ $# -ne 1 ]; then
  echo "one arg needed for commit message!"
  exit 1
fi

git add . && git commit -m "$1" && git push
