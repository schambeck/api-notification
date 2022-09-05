#!/bin/bash
echo "Deploying stack api-notification..."
docker stack deploy -c docker-compose.yml --with-registry-auth api-notification
echo "Stack deployed!"
