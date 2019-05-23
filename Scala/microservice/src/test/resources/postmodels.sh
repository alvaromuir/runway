#!/bin/sh
##
# Simple script that POSTs all files in a given directory with given args
# Alvaro Muir, Verizon IT Analytics Data Engineering
##

OPTIND=1 # Reset in case getopts has been used previously in the shell.
url="" 
path="./"

while getopts ":h" opt; do
  case ${opt} in
    h )
      echo "Usage:"
      echo "    $0 -h             Display this help message."
      echo "    $0 to <url>       to <url>."
      exit 0
      ;;
   \? )
     echo "Invalid Option: -$OPTARG" 1>&2
     exit 1
     ;;
  esac
done
shift $((OPTIND -1))

subcommand=$1; shift
case "$subcommand" in
  to)
    url=$1; shift

    # Process package options
    while getopts ":p:" opt; do
      case ${opt} in
        p )
          path=$OPTARG
          ;;
        \? )
          echo "Invalid Option: -$OPTARG" 1>&2
          exit 1
          ;;
        : )
          echo "Invalid Option: -$OPTARG requires an argument" 1>&2
          exit 1
          ;;
      esac
    done
    shift $((OPTIND -1))
    ;;
esac

for file in $path/*
do
  if [[ $file == *.pmml ]] || [[ $file == *.xml ]];
  then
    model=$(basename $file | sed 's/\.[^.]*$//')
    curl -X POST -F "name=$model" -F "project=runway" -F "file=@$file" -F "author=Alvaro Muir" $url
    fi
done;