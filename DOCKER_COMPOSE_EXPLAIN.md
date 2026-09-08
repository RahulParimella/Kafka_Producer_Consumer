# docker-compose.yml — line-by-line explanation

This file explains each line of the project's `docker-compose.yml` so you understand what each setting does and what to remember when changing it.

---

version: '3.8'
- Specifies the Compose file format version. `3.8` is compatible with modern Docker Engine and Compose v2. Use this to enable newer compose features.

services:
- Top-level key that lists all services (containers) that compose will create.

  zookeeper:
- Service name: `zookeeper`. This block contains configuration for the Zookeeper container.

    image: confluentinc/cp-zookeeper:7.3.0
    - The Docker image to pull and run. Using a tagged Confluent image ensures a known Kafka/Zookeeper compatibility.

    environment:
    - Environment variables passed to the container. These configure Zookeeper at runtime.

      ZOOKEEPER_CLIENT_PORT: 2181
      - Zookeeper client port inside the container.

      ZOOKEEPER_TICK_TIME: 2000
      - Basic time unit in Zookeeper (milliseconds). Many configs use this as a heartbeat/tick value.

    ports:
    - Port mappings from host to container.

      - "2181:2181"
      - Maps host port 2181 → container port 2181 so host tools (or other host services) can reach Zookeeper.

  kafka:
- Service name: `kafka`. Configures the Kafka broker container.

    image: confluentinc/cp-kafka:7.3.0
    - Kafka image; choose a version compatible with Zookeeper and the clients you use.

    depends_on:
      - zookeeper
    - Ensures Docker starts Zookeeper before Kafka. Note: `depends_on` controls start order but does not wait for readiness; use healthchecks or retries if needed.

    ports:
      - "9092:9092"
    - Maps host port 9092 → container port 9092. Useful for connecting host tools or mapping to external clients.

    environment:
    - Kafka-specific env vars.

      KAFKA_BROKER_ID: 1
      - Unique broker id for this Kafka broker. Use different IDs for multiple brokers.

      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      - How Kafka finds Zookeeper inside the Docker network (`zookeeper` is the service hostname).

      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      - The network interface and port Kafka listens on inside the container.

      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
      - What Kafka advertises to clients as its address. Inside the Compose network, use the service name `kafka` so other containers can reach it.
      - If you need host/desktop clients to connect to Kafka using `localhost:9092`, change this to include an advertised listener with the host's address (see notes below).

      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      - Replication factor for internal offsets topic. For single-broker local setups use `1`.

  order-producer:
- Service name for the producer application.

    build:
      context: ./Order_Producer
    - Build the Docker image from the `Order_Producer` folder using the `Dockerfile` there.

    ports:
      - "8081:8081"
    - Exposes the application's port to the host.

    environment:
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    - Sets the Spring Boot env var so the producer connects to the Kafka broker inside the Docker network using the service hostname.

    depends_on:
      - kafka
    - Start order-producer after Kafka has been started by Docker. This doesn't wait for Kafka readiness; the app should retry on startup if Kafka isn't ready yet.

  order-consumer:
- Service name for the consumer application; same pattern as producer.

    build:
      context: ./Order_Consumer
    ports:
      - "8082:8082"
    environment:
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    depends_on:
      - kafka

networks:
  default:
    name: kafka-net
- Defines the default network for the Compose app and gives it a stable name (`kafka-net`). Containers can use service names as hostnames on this network.

---

Additional notes / things to remember:

- depends_on does not wait for service readiness. For Kafka you may want the app to implement retries or add healthchecks in compose.
- `KAFKA_ADVERTISED_LISTENERS` is commonly the source of confusion: use internal hostnames (service names) for inter-container traffic and add a host-facing advertised listener when you need to connect from your host machine or external tools.
- Port mapping `host:container` lets you access services from your host. If you map Kafka to `9092:9092` and leave `KAFKA_ADVERTISED_LISTENERS` as `kafka:9092`, host clients may not connect correctly — set the advertised listener to the host address (e.g., `PLAINTEXT://localhost:9092`) or add multiple listeners.
- For production, use proper Kafka clusters (multiple brokers), set replication factors > 1, and secure the cluster (SASL/SSL). Do not run Confluent images with default configs in production without hardening.

If you'd like, I can:
- update `docker-compose.yml` to advertise Kafka on `localhost` for host tooling, or
- add healthchecks to Kafka and the apps so Compose waits for readiness.
