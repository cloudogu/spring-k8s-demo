FROM openjdk:8u212-jdk-slim-stretch
# we need curl for istio health checks
RUN apt-get -y update  \
 && apt-get -y install curl \
 && apt-get clean
COPY build/libs/spring-k8s-demo-*.jar /demo.jar
ENTRYPOINT /demo.jar
