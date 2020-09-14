#!/usr/bin/bash

webapps=$(pwd)

network=$1

if [ ! $network ];then
    echo 'Usage: config.sh <network>'
    echo 'Option:'
    echo '      network   - docker network name you want to connect'
    exit
fi

#cat $webapps/api/config/application.yml

# config docker network
if [ -f $webapps/docker-compose.yml ];then
  sed -i "s/name:[[:space:]]*\${network}/name: $network/" $webapps/docker-compose.yml
else
  echo docker-compose.yml not found!
fi


## start docker
#echo 'docker-compose up ...'
#docker-compose -f docker-compose.yml up
