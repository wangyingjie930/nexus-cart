package com.alibaba.nexus.cart.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Profile("!test") // Deactivate this bean when the 'test' profile is active
public class CartEventsConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CartEventsConsumer.class);

    /**
     * Consumes change data capture (CDC) events from the 'carts' table.
     * The topic name is determined by Debezium's configuration: <topic.prefix>.<schema_name>.<table_name>
     * - topic.prefix = 'cart-service-events' (from debezium-mysql-connector.json)
     * - schema_name = 'nexus_cart' (the database name)
     * - table_name = 'carts' (the table name)
     *
     * @param message The raw JSON message from Debezium, representing a change in the carts table.
     */
    @KafkaListener(topics = "cart-service-events.nexus_cart.carts", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeCartEvents(String message) {
        logger.info("Consumed CDC event from 'carts' table: {}", message);
        // In a real application, you would typically use Jackson's ObjectMapper
        // to deserialize this JSON string into a specific DTO to process it further.
    }
}
