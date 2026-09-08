# Docker notes — Order_Producer

This file documents what to remember when adding a `Dockerfile` and using `docker-compose` for the `Order_Producer` service.

## Build / image
- Dockerfile uses a multi-stage build: first stage builds with Maven, second runs the built jar.
- Keep `-DskipTests` only for local iteration; CI should run tests before building images.

## Ports & env
- Service port: `8081` (exposed in the Dockerfile). Map it in `docker-compose.yml` to access from host.
- Important env var: `SPRING_KAFKA_BOOTSTRAP_SERVERS` (set to `kafka:9092` in compose).

## JVM / runtime notes
- Consider passing JVM options via `JAVA_OPTS` or `JAVA_TOOL_OPTIONS` for memory tuning.
- Example compose override: `command: ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]`.

## Files & volumes
- Mount logs or config via volumes only when you need to persist or debug.

## Networking and Kafka
- In compose use the Kafka container hostname (e.g., `kafka:9092`) inside the Docker network.
- If you need to access Kafka from your host or other machines, set `KAFKA_ADVERTISED_LISTENERS` appropriately in compose (see root `docker-compose.yml`).

## Healthchecks
- Add a `HEALTHCHECK` to the Dockerfile or a `healthcheck` section in compose to detect failed starts.

## Security & production
- Do not use `*` for `spring.json.trusted.packages` in production.
- Use a non-root user to run the container where possible.

## Build & run commands

Build image locally:
```bash
docker build -t order-producer:local ./Order_Producer
```

Run with compose (building images):
```bash
docker compose up --build
```
