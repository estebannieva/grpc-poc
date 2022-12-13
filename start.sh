#!/bin/bash
curr=$(pwd)
cd microservices
export LOCAL_IP=$(ipconfig getifaddr en0)
docker-compose -p todolist up -d --force-recreate postgres pgadmin categories categories-grpc tasks tasks-grpc react
cd $curr
