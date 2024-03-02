#/bin/bash

./gradlew bootRun --args="--spring.flyway.enabled=false --spring.profiles.active=dev" --no-daemon &
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
curl -s -o ./schemas/address.graphql $schema_endpoint
echo "Stopping graphql server"
kill $gradlew_pid
