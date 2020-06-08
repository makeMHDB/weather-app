FROM java:8
VOLUME /tmp
EXPOSE 8080
ADD target/weatherapp-0.0.1-SNAPSHOT.jar weatherapp-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","weatherapp-0.0.1-SNAPSHOT.jar"]