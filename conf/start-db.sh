#!/bin/bash
source ./set_env.sh

docker run -d --name postgres-container -p 5432:5432 -e POSTGRES_PASSWORD=${DB_PASSWORD} postgres:latest

echo "Waiting for postgres to come up..."
count=0
until pg_isready -h "$PG_HOST" -p "$PG_PORT" -U postgres; do
  sleep 2
  ((count++))
  if [ $count -gt 5 ]; then
    echo "Waited 10 seconds, removing postgres-container and exiting."
    docker remove postgres-container
    exit 1
  fi
done

docker exec postgres-container psql -U postgres -c "CREATE USER ${DB_USER} WITH PASSWORD '${DB_PASSWORD}';"
docker exec postgres-container psql -U postgres -c "CREATE DATABASE chatapp;"

echo "Database and user created successfully!"