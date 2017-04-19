#! bin/bash

if [[ $# -lt 2 ]]; then
  echo "Argument error"
  exit -1
fi


echo $@ > $2