package com.shahrtech.keycloak.otp.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jboss.logging.Logger;
import com.shahrtech.keycloak.otp.kafka.KafkaProducerFactory;
import com.shahrtech.keycloak.otp.kafka.MessageKafkaDto;

import java.util.Map;
import java.util.concurrent.*;

public class KafkaProvider {
    private static final Logger logger = Logger.getLogger(KafkaProvider.class);
    private static final ConcurrentHashMap<String, KafkaProvider> instance = new ConcurrentHashMap<>();
    private final String topicEvents;
    private final Producer<String, String> producer;
    private final ObjectMapper mapper;

    private KafkaProvider(String bootstrapServers,
                          String clientId,
                          String topicEvents,
                          Map<String, Object> kafkaProducerProperties,
                          KafkaProducerFactory factory) {
        this.topicEvents = topicEvents;
        mapper = new ObjectMapper();
        producer = factory.createProducer(clientId, bootstrapServers, kafkaProducerProperties);
    }

    public static KafkaProvider getInstance(String realm) {
        logger.info(instance.toString());
        return instance.get(realm);
    }

    public static void setInstance(String realm,
                                   String bootstrapServers,
                                   String clientId,
                                   String topicEvents,
                                   Map<String, Object> kafkaProducerProperties,
                                   KafkaProducerFactory factory) {
        logger.info("Kafka provider set instance");
        instance.computeIfAbsent(realm, r -> {
            logger.info("Kafka provider set instance new instance");
            return new KafkaProvider(
                    bootstrapServers,
                    clientId,
                    topicEvents,
                    kafkaProducerProperties,
                    factory
            );
        });
    }

    private void produceEvent(String eventAsString, String topic) throws InterruptedException, ExecutionException, TimeoutException {
        logger.debug("Produce to topic: " + topicEvents + " ...");
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, eventAsString);
        Future<RecordMetadata> metaData = producer.send(record);
        RecordMetadata recordMetadata = metaData.get(30, TimeUnit.SECONDS);
        logger.debug("Produced to topic: " + recordMetadata.topic());
    }

    public void produce(MessageKafkaDto event) {
        try {
            produceEvent(mapper.writeValueAsString(event), topicEvents);
        } catch (JsonProcessingException | ExecutionException | TimeoutException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }
}
