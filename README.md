# Nexus Cart Service with Debezium and Kafka Connect

This project demonstrates a `cart-service` microservice integrated with Seata for distributed transactions and a full Change Data Capture (CDC) pipeline using Debezium, Kafka, and Kafka Connect, all orchestrated with Docker Compose.

## Prerequisites

- Docker
- Docker Compose

## Overview

The `docker-compose.yml` file sets up the following services:

- `mysql`: The primary database for the `cart-service` (`nexus_cart` schema) and Seata (`nexus_seata` schema).
- `zookeeper`: Required for Kafka.
- `kafka`: The message broker.
- `connect`: A Kafka Connect worker that is automatically built with the Debezium MySQL connector.
- `seata-server`: The transaction coordinator for distributed transactions.
- `cart-service`: The Java Spring Boot application.

## How to Run

### 1. Start the Environment

From the root of the project, run the following command to build the custom images and start all services in the background:

```bash
docker-compose up -d --build
```

This command will:
- Build the `cart-service` image from its `Dockerfile`.
- Build the custom `kafka-connect` image, which includes the Debezium MySQL connector.
- Start all the services defined in `docker-compose.yml`.

It may take a few minutes for all services to start up and become healthy.

### 2. Provision the Debezium Connector

Once the Kafka Connect service is running (which can take a minute or two), you can create the Debezium MySQL connector. This connector will monitor the `nexus_cart` database for any changes.

The configuration for the connector is located in the `debezium-mysql-connector.json` file.

To create the connector, run the following `curl` command from your terminal:

```bash
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" \
http://localhost:8083/connectors/ \
-d @debezium-mysql-connector.json
```

If the command is successful, you will see an `HTTP/1.1 201 Created` response.

### 3. Verify the Connector Status

You can check the status of the connector to ensure it's running correctly:

```bash
curl -s http://localhost:8083/connectors/cart-service-connector/status | jq
```

You should see `"connector": {"state": "RUNNING", ...}` and `"tasks": [{"state": "RUNNING", ...}]`.

### 4. Observe CDC Events in the Application

The `cart-service` itself is configured with a Kafka consumer that listens for changes from the `carts` table. When you add/update a cart, Debezium captures the change, and the service will consume its own event and log it.

You can view the logs of the `cart-service` to see these messages:

```bash
docker-compose logs -f cart-service
```

Look for log entries similar to this from the `CartEventsConsumer`:
```
INFO --- [ntainer#0-0-C-1] c.a.n.c.kafka.CartEventsConsumer         : Consumed CDC event from 'carts' table: {"schema":{...},"payload":{...}}
```

This demonstrates a complete, end-to-end CDC flow.

### 5. Observe Raw CDC Events in Kafka (Optional)

Any changes made to the tables in the `nexus_cart` database will now be captured by Debezium and sent to Kafka topics. The topics will be prefixed with `cart-service-events` as defined in the connector configuration. You can use any Kafka client to inspect these topics directly.

## How to Stop

To stop and remove all the containers, run:

```bash
docker-compose down
```
