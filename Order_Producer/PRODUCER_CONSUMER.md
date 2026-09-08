# Producer → Consumer (Order Service) — Simple Guide

This document explains how the `order-producer` sends events and how the `order-consumer` receives them, plus quick local test steps.

## Components

- **Producer application:** `Order_Producer` exposes a REST endpoint to create orders and sends them to Kafka using `KafkaTemplate<String, Order>`.
- **Consumer application:** `Order_Consumer` listens to the same topic via `@KafkaListener` and processes incoming `Order` objects.
- **Topic management:** a `NewTopic` bean (and optional `TopicManager`) controls topic creation and partition count.

## How messages are sent (producer)

1. Client calls POST `/orders` on the producer:

   - Controller: `OrderController.createOrder()` stores the order locally and calls `OrderProducerService.sendOrder(order)`.

2. `OrderProducerService.sendOrder(Order)` uses `KafkaTemplate.send(topic, key, value)`:

   - `topic` defaults to `order-topic`.
   - `key` is `order.getId().toString()` (ensures messages with same key route to the same partition).
   - `value` is the `Order` object which is serialized to JSON by `JsonSerializer`.

3. The send is asynchronous; code logs success (topic, partition, offset) or logs failures.

Files: `src/main/java/com/example/kafka/demo/controller/OrderController.java`, `src/main/java/com/example/kafka/demo/service/OrderProducerService.java`

## How messages are consumed (consumer)

1. `Order_Consumer` defines a listener method:

   - `@KafkaListener(topics = "order-topic", groupId = "order-service")`
   - Method signature accepts an `Order` parameter; `JsonDeserializer` converts JSON back to `Order`.

2. The listener executes business logic on each message (logging, processing, DB writes, etc.).

Files: `src/main/java/com/example/kafka/demo/service/OrderConsumerService.java`

## Serialization / Trusted packages

- Producer config (in `Order_Producer/src/main/resources/application.properties`) sets:

  ```properties
  spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
  ```

- Consumer config must use the matching deserializer and trust the model package:

  ```properties
  spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
  spring.kafka.consumer.properties.spring.json.trusted.packages=com.example.kafka.demo.model
  ```

  If deserialization fails during development, you can use `*` for trusted packages (not recommended for production).

## Topic partitions and keys

- Messages with the same key are mapped to the same partition. This preserves ordering for that key.
- Increasing partitions allows more parallelism for consumers in the same consumer group (one consumer thread per partition).
- Note: increasing partitions does not re-shuffle existing messages — it only affects future messages.

## Auto-increase partitions (optional)

- If `TopicManager` is enabled, it will use Kafka `AdminClient` on application startup to:
  - Describe the topic, check current partition count.
  - If current < configured `kafka.topic.partitions`, call `createPartitions` to increase to the target.

- Properties to configure (in `application.properties`):

  ```properties
  kafka.topic.name=order-topic
  kafka.topic.partitions=3
  kafka.topic.autoIncrease=true
  ```

## Quick local test (run both apps and POST an order)

1. Start Kafka locally (e.g., using Confluent Platform, local cluster, or Docker).
2. Start the producer and consumer apps:

```bash
mvn -f Order_Producer spring-boot:run
mvn -f Order_Consumer spring-boot:run
```

3. Send an order with `curl`:

```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"id":1,"product":"book","quantity":2}'
```

4. Check logs:

  - Producer logs: should show `Order sent successfully` with topic/partition/offset.
  - Consumer logs: should show `Received order from Kafka...` with order details.

## Troubleshooting

- If the consumer doesn't receive messages:
  - Verify both apps use the same `spring.kafka.bootstrap-servers`.
  - Confirm the topic exists and partitions >= 1 (use `kafka-topics.sh --describe`).
  - Ensure `spring.json.trusted.packages` includes the model package.

- If serialization errors occur: check that producer uses `JsonSerializer` and consumer uses `JsonDeserializer`.

## Next steps / safety

- For production, secure `trusted.packages` more narrowly and do not use `*`.
- Consider monitoring partition counts and consumer lag (e.g., via Kafka metrics or Prometheus).

---

See also `Order_Producer/HELP.md` for general project pointers.
