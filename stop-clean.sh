#!/bin/bash
curr=$(pwd)
cd microservices
export LOCAL_IP=$(ipconfig getifaddr en0)
docker-compose -p todolist down --remove-orphans --rmi all -v
cd $curr
