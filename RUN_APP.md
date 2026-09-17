# Run this Kafka Microservice Application

This project contains:
- `Order_Producer` on port `8081`
- `Order_Consumer` on port `8082`
- `Inventory_Service` on port `8083`
- Kafka broker on port `9092`
- Zookeeper on port `2181`
- PostgreSQL on port `5432`

## 1) Prerequisites

Install the following:
- Java 17 or later
- Maven
- Docker Desktop / Docker Engine
- PostgreSQL

Make sure PostgreSQL is running, and create a database named:

```sql
CREATE DATABASE orderdb;
```

If needed, set the database credentials in each service config:

- `Order_Producer/src/main/resources/application.properties`
- `Order_Consumer/src/main/resources/application.properties`
- `Inventory_Service/src/main/resources/application.properties`

Current config expects:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/orderdb
spring.datasource.username=postgres
spring.datasource.password=admin@123
```

---

## 2) Start Kafka and the app containers with Docker Compose

From the project root (`c:/Personal/Kafka`):

```bash
docker compose up --build
```

This starts:
- Zookeeper
- Kafka
- Order Producer
- Order Consumer
- Inventory Service

Wait until all containers are running.

### Useful docker commands

```bash
docker compose ps
```

```bash
docker compose logs -f
```

```bash
docker compose down
```

---

## 3) Run apps manually without Docker

If you want to run each Spring Boot service locally instead of using Docker:

### 3.1 Start Kafka locally

If you are not using Docker, start a Kafka broker and Zookeeper manually.

### 3.2 Start the producer

```bash
cd Order_Producer
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
cd Order_Producer
mvnw.cmd spring-boot:run
```

### 3.3 Start the consumer

```bash
cd Order_Consumer
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
cd Order_Consumer
mvnw.cmd spring-boot:run
```

### 3.4 Start the inventory service

```bash
cd Inventory_Service
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
cd Inventory_Service
mvnw.cmd spring-boot:run
```

---

## 4) Test the producer endpoint

Open a terminal and call the producer:

```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "product": "book",
    "quantity": 2
  }'
```

If the app works, the producer sends the message to Kafka and the consumer should process it.

---

## 5) Check logs

Producer logs should show the order being sent to Kafka.
Consumer logs should show the message received from the Kafka topic.
Inventory service logs should show processing of the event.

```bash
docker compose logs -f order-producer
```

```bash
docker compose logs -f order-consumer
```

```bash
docker compose logs -f inventory-service
```

---

## 6) Common issues

### Kafka not starting

Check if Docker is running.

```bash
docker ps
```

If Kafka has stale data, clear old Kafka metadata:

```bash
rmdir /s /q C:\tmp\kraft-combined-log
```

### PostgreSQL connection error

Verify PostgreSQL is running and the database exists:

```sql
SELECT datname FROM pg_database;
```

### App fails to connect to Kafka

Make sure Kafka is running on `localhost:9092` for local runs, or `kafka:9092` for Docker network communication.

---

## 7) Stop everything

```bash
docker compose down
```

Or stop the Spring Boot apps in the terminals with:

```bash
Ctrl + C
```

---

## 8) Recommended order of startup

For a clean run:

1. Start PostgreSQL
2. Start Kafka/Zookeeper
3. Start `Order_Producer`
4. Start `Order_Consumer`
5. Start `Inventory_Service`
6. Send test order via REST

---

## 9) Project root files you should know

- `docker-compose.yml` — starts all services together
- `Order_Producer/DOCKER.md` — producer notes
- `Order_Consumer/DOCKER.md` — consumer notes
- `DOCKER_COMPOSE_EXPLAIN.md` — explains the compose file
- `steps for Kafka setup.txt` — Kafka setup notes

This is the easiest way to run the full application.
