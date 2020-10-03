#!/usr/bin/bash

webapps=$(pwd)

network=$1

if [ ! $network ];then
    echo 'Usage: config.sh <network>'
    echo 'Option:'
    echo '      network   - docker network name you want to connect'
    exit
fi


get_nginx_container() {
   docker network inspect $network  --format '{{range .Containers}}{{$name := .Name}}{{println $name}}{{end}}' | grep nginx
}

gateway_container=$(get_nginx_container)

get_nginx_confd_path() {
   docker inspect $gateway_container --format '{{range .Mounts}}{{$path := .Source}}{{println $path}}{{end}}' | grep conf.d
}

gateway_confd=$(get_nginx_confd_path)

## copy sandbox.d/*.conf to $gateway_confd/sandbox.d
if [ -d sandbox.d ];then
  echo "cp sandbox.d/*.conf $gateway_confd/sandbox.d"
  if [ ! -d  $gateway_confd/sandbox.d ];then mkdir $gateway_confd/sandbox.d fi
  cp sandbox.d/*.conf $gateway_confd/sandbox.d
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

