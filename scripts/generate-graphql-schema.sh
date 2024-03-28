#/bin/bash

export SPRING_R2DBC_URL=r2dbc:postgresql://localhost
export SPRING_FLYWAY_URL=jdbc:postgresql://localhost
export SPRING_R2DBC_USERNAME=postgres
export SPRING_R2DBC_PASSWORD=postgres
export SPRING_FLYWAY_ENABLED=false

./gradlew bootRun --no-daemon &
gradlew_pid=$!
schema_endpoint="http://localhost:8080/sdl"
c=0
until curl -s -f -o /dev/null $schema_endpoint
do
    ((c++))
    if ((c > 200)); then
        echo "Failed to get graphql schema: timeout"
        exit 1
    fi
    echo "Waiting for server"
    sleep 2
done
curl -s -o ./schemas/discount.graphql $schema_endpoint
echo "Stopping graphql server"
kill $gradlew_pid
