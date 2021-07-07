FROM openjdk:11
WORKDIR /
ADD target/selenium-integration-0.0.1-SNAPSHOT.jar selenium-integration-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD ["java", "-jar", "selenium-integration-0.0.1-SNAPSHOT.jar"]