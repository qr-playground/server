#!/usr/bin/env bash
# docker-compose-run.sh
# chmod +x docker-compose-run.sh 
set -e

echo "▶ Gradle build…"
./gradlew clean build -x test

echo "▶ Docker down…"
docker-compose down

echo "▶ Docker run…"
docker-compose build --no-cache

docker-compose up -d 