#!/bin/bash

ELASTIC_WAITS=0

# wait for Elasticsearch to come up
while ! curl -s -u "${ELASTIC_USERNAME}:${ELASTIC_PASSWORD}" -I "http://${ELASTIC_URL}" >/dev/null 2>&1; do
  ((ELASTIC_WAITS++))
  echo "Waiting for Elasticsearch... (Attempt $ELASTIC_WAITS/10)"
  if [ $ELASTIC_WAITS -eq 10 ]; then
    echo "aw man, Elasticsearch did not come up :(, exiting"
    exit 1
  fi
  sleep 6
done

echo "Elasticsearch is up!"

# create the messages index, if it does not exist
STATUS=$(curl -s -o /dev/null -w "%{http_code}" -u "${ELASTIC_USERNAME}:${ELASTIC_PASSWORD}" "http://${ELASTIC_URL}/messages")

if [ "$STATUS" -eq 404 ]; then
  echo "Messages index not found, creating it"
  curl -s -u "${ELASTIC_USERNAME}:${ELASTIC_PASSWORD}" -X PUT "http://${ELASTIC_URL}/messages" \
  -H 'Content-Type: application/json' \
  -d '{
    "mappings": {
      "properties": {
        "user": { "type": "keyword" },
        "room": { "type": "keyword" },
        "text": { "type": "text" },
        "timestamp": { "type": "date" }
      }
    }
  }'
fi


exec java -jar chatapp-backend-0.0.1-SNAPSHOT.jar