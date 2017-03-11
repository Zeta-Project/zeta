#!/usr/bin/env bash

httpstatus="000"

printf "Waiting for server to start\n"
while [ $httpstatus -ne "200" ]; do
  httpstatus=$(curl -s -o /dev/null -I -w "%{http_code}" http://couchbase-server:8091/pools/default/buckets/db)
done

sleep 5

sync_gateway /home/sync_gateway/sync_gateway.json

while true; do sleep 1000; done
