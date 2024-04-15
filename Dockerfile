FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /application

COPY pom.xml .
COPY src src
COPY src/main/resources/fashion.csv .


RUN mvn clean package -DskipTests

FROM openjdk:17

COPY --from=build /application/target/Ecommerce-0.0.1-SNAPSHOT.jar /Ecommerce-0.0.1-SNAPSHOT.jar

RUN ls /

ENTRYPOINT ["java", "-jar", "/Ecommerce-0.0.1-SNAPSHOT.jar"]