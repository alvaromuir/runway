#!/bin/sh
##
# Simple script that creates necessary config file for runway program
# Alvaro Muir, Verizon IT Analytics Data Engineering
# 04.19.2017
##

function usage {
cat <<EOM
Usage: $(basename "$0") [OPTION]...

  -f <fqdn>     fuly-qualified domain address
                e.g.: http://server.verizon.com

  -p <port>     port application will bind to
                e.g.: 80
                defaults to 8080

  -u <log path> target path for server logs
                e.g. /tmp
                defaults to ./

  -l <log path> target path for server logs
                e.g. /var/log
                defaults to ./

  -v            verbose output

  -h          display help
EOM

	exit 2
}

function recap {
  echo "Processed:"
  echo "fqdn=$fqdn"
  echo "port=$port"
  echo "data upload path=$dataUploadPath"
  echo "app logging path=$loggingPath"
  echo "configuration path=$configPath"
  echo
  echo
  echo "-----------------------------------------"
  echo "A total of $# args remain:"
  echo "$*"
  echo "run \""$(basename \"$0\")" with -h for more info"
  echo "-----------------------------------------"
  echo
  echo
  echo "config written as:"
  cat $configPath
  echo
  echo
}

fqdn="$(hostname -f)"
port="8080"
dataUploadPath="./"
loggingPath="./"
configPath="./application.conf"

while getopts ":f:p:u:l:c:vh" optKey; do
	case $optKey in
		f)
			fqdn=$OPTARG
			;;
    p)
			port=$OPTARG
			;;
		u)
			dataUploadPath=$OPTARG
			;;
		l)
			loggingPath=$OPTARG
			;;
    c)
			configPath=$OPTARG
			;;
    v)
      recap
      ;;
		h|*)
			usage
			;;
	esac
done


shift $((OPTIND - 1))

# open file decription
exec 3<> $configPath

# writing file
echo "http {" >&3
echo "  host: \"$fqdn\"," >&3
echo "  port: $port," >&3
echo "  dataUploadPath: \"$dataUploadPath\"," >&3
echo "  timeOut: 3," >&3
echo "  dataLineLimit: 1024," >&3
echo "  logPath: \"$loggingPath\"" >&3
echo "}" >&3
echo "" >&3
echo "akka {" >&3
echo "  http {" >&3
echo "    server {" >&3
echo "      parsing.max-content-length: infinite," >&3
echo "      idle-timeout: infinite," >&3
echo "      request-timeout: infinite" >&3
echo "    }" >&3
echo "  }" >&3
echo "  loggers = [\"akka.event.slf4j.Slf4jLogger\"]" >&3
echo "  logLevel = \"INFO\"" >&3
echo "  logging-filter = \"akka.event.slf4j.Slf4jLoggingFilter\"" >&3
echo "}" >&3

#close out
exec 3>&-