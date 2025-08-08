# Base image for Kafka Connect, matching other Confluent Platform components
FROM confluentinc/cp-kafka-connect-base:7.3.2

# Define arguments for version and URL for clarity and easy updates
ARG DEBEZIUM_VERSION=2.5.0.Final
ARG DEBEZIUM_PLUGIN_URL=https://repo1.maven.org/maven2/io/debezium/debezium-connector-mysql/${DEBEZIUM_VERSION}/debezium-connector-mysql-${DEBEZIUM_VERSION}-plugin.tar.gz

# The CONNECT_PLUGIN_PATH in the base image includes /usr/share/java
# We will create a subdirectory for our connector there.
USER root
RUN mkdir -p /usr/share/java/debezium-connector-mysql && \
    # Download the connector archive, extract it, and clean up
    curl -L -o /tmp/debezium-connector.tar.gz ${DEBEZIUM_PLUGIN_URL} && \
    tar -xzf /tmp/debezium-connector.tar.gz -C /usr/share/java/debezium-connector-mysql --strip-components 1 && \
    rm /tmp/debezium-connector.tar.gz && \
    # Ensure the user running Kafka Connect has the correct permissions
    chown -R appuser:appuser /usr/share/java/debezium-connector-mysql

USER appuser
