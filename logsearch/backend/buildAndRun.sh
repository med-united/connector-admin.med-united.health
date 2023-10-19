#!/bin/sh
mvn clean package && docker build -t health.medunited/logsearch .
docker rm -f logsearch || true && docker run -d -p 8080:8080 -p 4848:4848 --name logsearch health.medunited/logsearch 
