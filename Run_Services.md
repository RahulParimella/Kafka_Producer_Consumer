# Run the services with a local Kafka server

This project is configured to connect to Kafka at `localhost:9092` and PostgreSQL at `localhost:5432` when you run the apps on the host machine.

The application uses these ports:

- Order Producer: `http://localhost:8081`
- Order Consumer: `http://localhost:8082`
- Inventory Service: `http://localhost:8083`
- Kafka broker: `localhost:9092`
- PostgreSQL: `localhost:5432`

## Prerequisites

- Java 17+
- Maven
- Kafka broker running locally
- PostgreSQL running locally

## 1) Make sure PostgreSQL is running

Create the database:

```sql
CREATE DATABASE orderdb;
```

The app configuration expects:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/orderdb
spring.datasource.username=postgres
spring.datasource.password=admin@123
```

If your password or database name is different, update the files below before running:

- `Order_Producer/src/main/resources/application.properties`
- `Order_Consumer/src/main/resources/application.properties`
- `Inventory_Service/src/main/resources/application.properties`

## 2) Start your local Kafka server

Make sure Kafka is already running and reachable on:

```text
localhost:9092
```

If you use a local Kafka setup, this is the expected bootstrap server for the Spring Boot apps.

## 3) Run the producer

Open a new terminal and run:

```powershell
cd Order_Producer
mvnw.cmd spring-boot:run
```

The producer app should start on port `8081`.

## 4) Run the consumer

Open another terminal and run:

```powershell
cd Order_Consumer
mvnw.cmd spring-boot:run
```

The consumer app should start on port `8082`.

## 5) Run the inventory service

Open another terminal and run:

```powershell
cd Inventory_Service
mvnw.cmd spring-boot:run
```

The inventory service should start on port `8083`.

## 6) Test the flow

Send a sample order to the producer:

```powershell
curl -X POST http://localhost:8081/orders `
  -H "Content-Type: application/json" `
  -d '{"id":1,"product":"book","quantity":2}'
```

If everything is working:

- the producer sends the message to Kafka
- the consumer receives it
- the inventory service processes the event

## 7) Useful checks

Check the producer:

```powershell
curl http://localhost:8081/actuator/health
```

Check the consumer:

```powershell
curl http://localhost:8082/actuator/health
```

Check the inventory service:

```powershell
curl http://localhost:8083/actuator/health
```

## 8) If you want to create a Kafka topic manually

Run this in a Kafka terminal:

```powershell
kafka-topics.bat --create --topic order-topic --bootstrap-server localhost:9092 --partitions 4 --replication-factor 1
```

List topics:

```powershell
kafka-topics.bat --list --bootstrap-server localhost:9092
```

## 9) Stop the apps

Use:

```powershell
Ctrl + C
```

in each terminal to stop the Spring Boot services.

## 10) Recommended startup order

1. Start PostgreSQL
2. Start local Kafka
3. Start `Order_Producer`
4. Start `Order_Consumer`
5. Start `Inventory_Service`
6. Send a test order


