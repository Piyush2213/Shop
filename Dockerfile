FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /application

COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src src
COPY src/main/resources/fashion.csv .


RUN mvn clean package -DskipTests

FROM openjdk:17

COPY --from=build /application/target/ecommerce-0.0.1-SNAPSHOT.jar /ecommerce-0.0.1-SNAPSHOT.jar

COPY run.sh /run.sh
RUN chmod +x /run.sh

EXPOSE 8080

ENTRYPOINT ["/run.sh"]