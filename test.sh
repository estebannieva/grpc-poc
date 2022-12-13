#!/bin/bash
if [ $# -eq 0 ]; then
  echo "Category id argument required"
  exit 1
fi
category_id=$1
curr=$(pwd)
cd microservices
export LOCAL_IP=$(ipconfig getifaddr en0)
LANG=en_us_8859_1
TASKS_HOST=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' $(docker ps -a -q --filter "name=todolist.tasks.1"))
HTTP_TASKS_PORT=8081
GRPC_TASKS_PORT=50051
# Start containers
docker-compose -p todolist up -d influxdb grafana
# Execute REST API test
echo "==> Using GET /api/v1/tasks to get all tasks"
echo "==> Make secure that you import the file: microservices/tasks-tb.csv"
echo "==> Starting at time: $(date)"
start=$(date +%s)
docker-compose -p todolist run --rm -e HOST=$TASKS_HOST -e PORT=$HTTP_TASKS_PORT -e CATEGORY_ID=$category_id -v $PWD/k6:/k6 k6 run /k6/list_tasks_test.js
end=$(date +%s)
runtime=$((end-start))
echo "==> Open Grafana to see the results: http://localhost:3000"
echo "==> Ended at time: $(date)"
echo "==> Execution time: $(($runtime / 60)) minutes and $(($runtime % 60)) seconds"
# Wait to continue
read -p "Press ENTER to continue... " key
# Execute gRPC test
echo "==> Using gRPC listTasks to get all tasks"
echo "==> Make secure that you import the file: microservices/task-tb.csv"
echo "==> Starting at time: $(date)"
start=$(date +%s)
docker-compose -p todolist run --rm ghz ghz --name 'List Tasks' --insecure --proto /ghz/tasks.proto --call tasks.TaskService.listTasks --data '{ "id": "c0a87007-818d-1222-8181-8d1232f80000" }' --concurrency 1 --total 1000 --timeout 0 -O html $TASKS_HOST:$GRPC_TASKS_PORT > ../test/ghz-gRPC.html
end=$(date +%s)
runtime=$((end-start))
echo "==> Open the HTML file to see the results: test/ghz-gRPC.html"
echo "==> Ended at time: $(date)"
echo "==> Execution time: $(($runtime / 60)) minutes and $(($runtime % 60)) seconds"
containers=$(docker container ls -a --filter "name=todolist.grafana.1" --filter "name=todolist.influxdb.1" -q)
docker container stop $containers
docker container rm $containers
#docker system prune -a --filter "label=com.docker.compose.service=influxdb" --filter "label=com.docker.compose.service=grafana" -f --volumes
cd $curr
