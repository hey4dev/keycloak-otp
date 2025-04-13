package com.shahrtech.keycloak.otp.kafka;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

public final class KafkaStandardProducerFactory implements KafkaProducerFactory {

	@Override
	public Producer<String, String> createProducer(String clientId, String bootstrapServer,
			Map<String, Object> optionalProperties) {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId + UUID.randomUUID());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.putAll(optionalProperties);
		props.put("id", UUID.randomUUID().toString());
		return new KafkaProducer<>(props);
	}
}
