package com.shahrtech.keycloak.otp.factory;

import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;
import com.shahrtech.keycloak.otp.kafka.KafkaProducerConfig;
import com.shahrtech.keycloak.otp.kafka.KafkaStandardProducerFactory;
import com.shahrtech.keycloak.otp.provider.RequestOtpProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestOtpProviderFactory implements RealmResourceProviderFactory {
    Logger logger = Logger.getLogger(RequestOtpProviderFactory.class);
    private static final ConcurrentHashMap<String, RealmResourceProvider> instance = new ConcurrentHashMap<>();
    private Scope scope;

    public static final String ID = "otpsms";

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        String realm = session.getContext().getRealm().getName();
        return instance.computeIfAbsent(realm, r -> {
            logger.info("Init kafka module ...");
            String realmName = realm.toUpperCase();
            String topicEvents = scope.get("topicEvents", System.getenv(realmName + "_KAFKA_TOPIC"));
            String clientId = scope.get("clientId", System.getenv(realmName + "_KAFKA_CLIENT_ID"));
            String bootstrapServers = scope.get("bootstrapServers", System.getenv(realmName + "_KAFKA_BOOTSTRAP_SERVERS"));

            if (topicEvents == null) {
                throw new NullPointerException("topic must not be null.");
            }

            if (clientId == null) {
                throw new NullPointerException("clientId must not be null.");
            }

            if (bootstrapServers == null) {
                throw new NullPointerException("bootstrapServers must not be null");
            }

            Map<String, Object> kafkaProducerProperties = KafkaProducerConfig.init(scope);

            return new RequestOtpProvider(session, bootstrapServers, clientId, topicEvents, kafkaProducerProperties, new KafkaStandardProducerFactory());
        });
    }

    @Override
    public void init(Scope config) {
        scope = config;
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
