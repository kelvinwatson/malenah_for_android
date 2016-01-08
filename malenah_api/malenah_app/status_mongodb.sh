#!/usr/bin/env bash

systemctl status mongod
echo '\n'
tail -1 /var/log/mongodb/mongod.log

#forever start /root/beerApi/index.js &
#forever start /root/Website/index.js &
