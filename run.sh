#!/bin/bash

PROFILE=${1:-dev}

if [[ "$PROFILE" != "dev" && "$PROFILE" != "prod" ]]; then
    echo "Error: Invalid profile '$PROFILE'."
    echo "Usage: ./run.sh [dev|prod]"
    exit 1
fi

COMPOSE_FILE="docker/docker-compose.${PROFILE}.yml"
ENV_FILE="environments/.env"

if [[ ! -f "$COMPOSE_FILE" ]]; then
    echo "Error: Compose file $COMPOSE_FILE does not exist!"
    exit 1
fi

echo "Starting book-store with '$PROFILE' profile..."
echo "Using compose file: $COMPOSE_FILE"
echo "Using env file: $ENV_FILE"

docker compose --env-file "$ENV_FILE" -f "$COMPOSE_FILE" up --build -d

echo ""
echo "Done! To view live logs run:"
echo "   docker compose -f $COMPOSE_FILE logs -f <container-name>"
