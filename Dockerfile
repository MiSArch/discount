FROM gradle:jdk17
WORKDIR /home/gradle/app
ADD . .
ARG module
RUN gradle clean build

FROM eclipse-temurin:17
ARG module
WORKDIR /home/java
COPY --from=0 /home/gradle/app/build/libs/*.jar app.jar
CMD java -jar ./app.jar