FROM openjdk:8u212-jdk-slim-stretch
COPY build/libs/spring-k8s-demo-*.jar /demo.jar
ENTRYPOINT /demo.jar
