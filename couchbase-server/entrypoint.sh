#!/usr/bin/env bash

cd / && ./entrypoint.sh couchbase-server &

httpstatus="000"

printf "Waiting for server to start\n"
while [ $httpstatus -ne "301" ]; do
  httpstatus=$(curl -s -o /dev/null -I -w "%{http_code}" http://127.0.0.1:8091/)
done

bucketstatus=$(curl -s -o /dev/null -I -w "%{http_code}" http://127.0.0.1:8091/pools/default/buckets/$BUCKET)

if [ $bucketstatus != 200 ]
  then

    printf "Registering user\n"

    curl -s -X POST http://127.0.0.1:8091/settings/web -d port=8091 -d username=$CB_USER -d password=$CB_PASS

    printf "\nCreating bucket\n"

    curl -s -X POST -u $CB_USER:$CB_PASS \
    	-d name=$BUCKET -d ramQuotaMB=200 -d authType=sasl \
    	http://127.0.0.1:8091/pools/default/buckets
fi

printf "Server ready\n"

while true; do sleep 1000; done
