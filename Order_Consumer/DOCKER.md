# Docker notes — Order_Consumer

This file documents what to remember when adding a `Dockerfile` and using `docker-compose` for the `Order_Consumer` service.

## Build / image
- Dockerfile uses a multi-stage build (Maven build stage + runtime image). Ensure `pom.xml` and `src` are copied for the build stage.

## Ports & env
- Service port: `8082` (exposed in the Dockerfile). Map it in `docker-compose.yml` to access from host if needed.
- Important env var: `SPRING_KAFKA_BOOTSTRAP_SERVERS` (set to `kafka:9092` in compose).

## Deserialization notes
- Ensure `spring.kafka.consumer.properties.spring.json.trusted.packages` includes the model package (for example `com.example.kafka.demo.model`) so `JsonDeserializer` can instantiate `Order`.

## Logging & volumes
- If you want to persist logs, mount a host volume. For troubleshooting, exposing logs to the host is useful.

## Consumer scaling
- Parallelism is driven by partitions: increase topic partitions and run one consumer instance per partition (in the same group) for parallel consumption.

## Healthchecks & graceful shutdown
- Add `HEALTHCHECK` or a readiness probe to ensure the consumer is connected to Kafka.
- Ensure the app handles SIGTERM for graceful Kafka listener shutdowns.

## Build & run commands

Build locally:
```bash
docker build -t order-consumer:local ./Order_Consumer
```

Run with compose (building images):
```bash
docker compose up --build
```
