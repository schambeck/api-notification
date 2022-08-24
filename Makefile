APP = api-notification
VERSION = 1.0.0
JAR = ${APP}-${VERSION}.jar
TARGET_JAR = target/${JAR}
#JAVA_OPTS = -XX:+UseSerialGC -Xss512k -XX:MaxRAM=72m -Dspring.main.lazy-initialization=true -Dspring.config.location=classpath:/application.properties
#JAVA_OPTS = -Dserver.port=8081 -javaagent:new-relic/newrelic.jar
JAVA_OPTS = -Dserver.port=0

#DOCKER_IMAGE = schambeck.jfrog.io/schambeck-docker/${APP}:latest
DOCKER_IMAGE = ${APP}:latest
DOCKER_FOLDER = src/main/docker
DOCKER_CONF = ${DOCKER_FOLDER}/Dockerfile
COMPOSE_CONF = ${DOCKER_FOLDER}/docker-compose.yml
REPLICAS = 1

AB_FOLDER = ab-results
AB_TIME = 10
AB_CONCURRENCY = 5

BASE_URL = http://localhost:8090
NOTIFICATION_ENDPOINT = ${BASE_URL}/notifications
SSE_ENDPOINT = ${BASE_URL}/sse
TOKEN =

# Maven

clean:
	./mvnw clean

all: clean
	./mvnw compile

install: clean
	./mvnw install

check: clean
	./mvnw verify

check-unit: clean
	./mvnw test

check-integration: clean
	./mvnw integration-test

dist: clean
	./mvnw package -Dmaven.test.skip=true

dist-run: dist run

run:
	java ${JAVA_OPTS} -jar ${TARGET_JAR}

# Docker

dist-docker-build: dist docker-build

dist-docker-build-push: dist docker-build docker-push

docker-build:
	DOCKER_BUILDKIT=1 docker build -f ${DOCKER_CONF} -t ${DOCKER_IMAGE} --build-arg=JAR_FILE=${JAR} target

docker-run:
	docker run -d \
		--restart=always \
		--net schambeck-bridge \
		--name ${APP} \
		--env DISCOVERY_URI=http://srv-discovery:8761/eureka \
		--env AUTH_URI=http://srv-authorization-kc:9000 \
		--env SPRING_SQL_INIT_MODE=always \
		--env SPRING_RABBITMQ_HOST=rabbitmq \
		--env SPRING_RABBITMQ_PORT=5672 \
		--env SPRING_RABBITMQ_VIRTUAL_HOST= \
		--env SPRING_RABBITMQ_USERNAME=guest \
		--env SPRING_RABBITMQ_PASSWORD=guest \
		--publish 8080:8080 \
		${DOCKER_IMAGE}

--rm-docker-image:
	docker rmi ${DOCKER_IMAGE}

docker-bash:
	docker exec -it docker_web_1 /bin/bash

docker-tag:
	docker tag ${APP} ${DOCKER_IMAGE}

docker-push:
	docker push ${DOCKER_IMAGE}

docker-pull:
	docker pull ${DOCKER_IMAGE}

docker-cp-jar:
	cp ${TARGET_JAR} ${DOCKER_FOLDER}

dist-docker-build-cp-jar: dist docker-build docker-cp-jar

# Docker Compose

dist-compose-up: dist compose-up

compose-up:
	docker-compose -p ${APP} -f ${COMPOSE_CONF} up -d --build

compose-down: --compose-down

compose-down-rmi: --compose-down --rm-docker-image

--compose-down:
	docker-compose -f ${COMPOSE_CONF} down

compose-logs:
	docker-compose -f ${COMPOSE_CONF} logs -f \web

# Docker Swarm

docker-service-inspect:
	docker service inspect ${APP}

docker-stack-deploy:
	cd ${DOCKER_FOLDER} && docker stack deploy -c <(docker-compose config) ${APP}

dist-docker-build-cp-jar-stack-deploy: dist-docker-build-cp-jar docker-stack-deploy

docker-service-rm-web:
	docker service rm ${APP}_web

docker-service-rm-db:
	docker service rm ${APP}_db

docker-service-rm-nginx:
	docker service rm ${APP}_nginx

docker-service-rm-haproxy:
	docker service rm ${APP}_haproxy

docker-service-rm-eureka:
	docker service rm ${APP}_eureka

docker-service-rm: docker-service-rm-web docker-service-rm-db docker-service-rm-haproxy

# HTTPie

httpie-create:
	http POST ${NOTIFICATION_ENDPOINT} \
	    'Authorization:Bearer ${TOKEN}'
		type=SSE \
		title='DNA created' \
		message='DNA has been created' \
		link=/mutant/fe6d3cfa-7e45-4ccf-bbb9-ac5ff0b84988

httpie-list:
	http GET ${NOTIFICATION_ENDPOINT}/queries/find-by-user 'Authorization:Bearer ${TOKEN}'

httpie-sse:
	http --stream GET ${SSE_ENDPOINT}?auth=${TOKEN}

ID_NOTIFICATION = 7d5e5211-14e9-4d7e-bcbc-02475cce0828

httpie-mark-as-read:
	http PUT ${NOTIFICATION_ENDPOINT}/${ID_NOTIFICATION}/actions/mark-as-read 'Authorization:Bearer ${TOKEN}'

httpie-count-unread:
	http GET ${NOTIFICATION_ENDPOINT}/queries/count-unread 'Authorization:Bearer ${TOKEN}'

# Apache Benchmark

ab-count-unread:
	ab -H "Authorization: Bearer ${TOKEN}" -T application/json -t ${AB_TIME} -c ${AB_CONCURRENCY} ${NOTIFICATION_ENDPOINT}/queries/count-unread > ${AB_FOLDER}/count-unread-c${AB_CONCURRENCY}.txt
