package org.metranet.keycloak.otp.provider;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;
import org.metranet.keycloak.otp.api.RequestOtpController;
import org.metranet.keycloak.otp.kafka.KafkaProducerFactory;

import java.util.Map;

public class RequestOtpProvider implements RealmResourceProvider {
    private final KeycloakSession session;
    public RequestOtpProvider(KeycloakSession session,
                              String bootstrapServers,
                              String clientId,
                              String topicEvents,
                              Map<String, Object> kafkaProducerProperties,
                              KafkaProducerFactory factory) {
        KafkaProvider.setInstance(bootstrapServers, clientId, topicEvents, kafkaProducerProperties, factory);
        this.session = session;
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public Object getResource() {
        return new RequestOtpController(session);
    }
}
