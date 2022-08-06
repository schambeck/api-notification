#!/bin/bash
echo "Deploying api-notification application to the Docker Swarm..."
docker stack deploy -c <(cd src/main/docker && docker-compose config) api-notification
echo "Application deployed!"
