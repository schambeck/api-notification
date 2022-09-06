# api-notification
[![build](https://github.com/schambeck/api-notification/actions/workflows/maven.yml/badge.svg)](https://github.com/schambeck/api-notification/actions/workflows/maven.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=schambeck_api-notification&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=schambeck_api-notification)

## User notification service through Server-sent events

### Initialize Swarm

    docker swarm init

### Build artifact

    ./mvnw clean package

Executable file generated: target/api-dna-1.0.0.jar

### Build docker image

    make docker-build

### Deploy infra stack

    make stack-srv-deploy

### Deploy app stack

    make stack-deploy

### Swagger

    http://api-notification.localhost

### Actuator

    http://api-notification.localhost/actuator

### Config Server

    http://localhost:8888/api-notification/default
