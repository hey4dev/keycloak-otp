package org.metranet.keycloak.otp.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;
import org.metranet.keycloak.otp.kafka.KafkaProducerConfig;
import org.metranet.keycloak.otp.kafka.KafkaStandardProducerFactory;
import org.metranet.keycloak.otp.provider.RequestOtpProvider;

import java.util.Map;

public class RequestOtpProviderFactory implements RealmResourceProviderFactory {
    Logger logger = Logger.getLogger(RequestOtpProviderFactory.class);
    private RealmResourceProvider instance;
    private String bootstrapServers;
    private String topicEvents;
    private String clientId;
    private Map<String, Object> kafkaProducerProperties;

    public static final String ID = "otpsms";
    
    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        if (instance == null) {
            instance = new RequestOtpProvider(session, bootstrapServers, clientId, topicEvents, kafkaProducerProperties, new KafkaStandardProducerFactory());
        }

        return instance;
    }

    @Override
    public void init(Scope config) {
        logger.info("Init kafka module ...");
        topicEvents = config.get("topicEvents", System.getenv("KAFKA_TOPIC"));
        clientId = config.get("clientId", System.getenv("KAFKA_CLIENT_ID"));
        bootstrapServers = config.get("bootstrapServers", System.getenv("KAFKA_BOOTSTRAP_SERVERS"));

        if (topicEvents == null) {
            throw new NullPointerException("topic must not be null.");
        }

        if (clientId == null) {
            throw new NullPointerException("clientId must not be null.");
        }

        if (bootstrapServers == null) {
            throw new NullPointerException("bootstrapServers must not be null");
        }

        kafkaProducerProperties = KafkaProducerConfig.init(config);
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // do nothing
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public String getId() {
        return ID;
    }

}
