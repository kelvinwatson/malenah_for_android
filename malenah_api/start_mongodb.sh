#!/usr/bin/env bash
service mongod start
systemctl status mongod
tail -1 /var/log/mongodb/mongod.log
