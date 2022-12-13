#!/bin/bash
docker ps -a --filter "name=todolist" --format "table {{.ID}}\t{{.Names}}\t{{.Image}}\t{{.Ports}}\t{{.Status}}"
