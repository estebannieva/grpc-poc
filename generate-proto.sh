#!/bin/bash
curr=$(pwd)
cd front-end/src/proto
protoc -I=. categories.proto tasks.proto --js_out=import_style=commonjs,binary:. --grpc-web_out=import_style=commonjs,mode=grpcwebtext:.
cd $curr
