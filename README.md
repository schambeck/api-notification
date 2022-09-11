# Notification API
[![build](https://github.com/schambeck/api-notification/actions/workflows/maven.yml/badge.svg)](https://github.com/schambeck/api-notification/actions/workflows/maven.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=schambeck_api-notification&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=schambeck_api-notification)

## User notification service through Server-sent events

### Build artifact

    ./mvnw clean package

Executable file generated: target/api-notification-1.0.0.jar

### Build docker image

    make docker-build

### Initialize Swarm

    docker swarm init

### Deploy infra stack

    make stack-srv-deploy

Check [api-dna](https://github.com/schambeck/api-dna) project.

### Deploy app stack

    make stack-deploy

### Swagger

    http://api-notification.localhost

### Actuator

    http://api-notification.localhost/actuator

### Config Server

    http://localhost:8888/api-notification/default
