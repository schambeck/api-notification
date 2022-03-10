APP = api-notification
VERSION = 0.0.1-SNAPSHOT
JAR = target/${APP}-${VERSION}.jar

BASE_URL_NOTIFICATION = http://localhost:9000
NOTIFICATION_ENDPOINT = ${BASE_URL_NOTIFICATION}/notifications
SSE_ENDPOINT = ${BASE_URL_NOTIFICATION}/sse

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
	java -jar ${JAR}

# HTTPie

httpie-create:
	http POST ${NOTIFICATION_ENDPOINT} \
		type=SSE \
		title='DNA created' \
		message='DNA has been created' \
		link=/mutant/fe6d3cfa-7e45-4ccf-bbb9-ac5ff0b84988

TOKEN =

httpie-list:
	http --style=pie-dark -vv -A bearer -a ${TOKEN} GET ${NOTIFICATION_ENDPOINT}/queries/find-by-user

httpie-sse:
	http --style=pie-dark -vv --stream GET ${SSE_ENDPOINT}?auth=${TOKEN}

ID_NOTIFICATION = 7d5e5211-14e9-4d7e-bcbc-02475cce0828

httpie-mark-as-read:
	http --style=pie-dark -vv PUT ${NOTIFICATION_ENDPOINT}/${ID_NOTIFICATION}/actions/mark-as-read

httpie-count-unread:
	http --style=pie-dark -vv GET ${NOTIFICATION_ENDPOINT}/queries/count-unread
