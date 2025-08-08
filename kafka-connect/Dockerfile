# Use the base image for Kafka Connect, matching the version of other CP components
FROM confluentinc/cp-kafka-connect-base:7.3.2

# Use Confluent Hub to install the Debezium MySQL connector
# In a production environment, it's recommended to use a specific version instead of 'latest'
RUN confluent-hub install --no-prompt debezium/debezium-connector-mysql:latest
