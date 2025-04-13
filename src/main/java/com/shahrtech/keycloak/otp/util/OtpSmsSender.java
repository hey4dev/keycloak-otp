package com.shahrtech.keycloak.otp.util;

import com.shahrtech.keycloak.otp.kafka.MessageKafkaDto;
import com.shahrtech.keycloak.otp.provider.KafkaProvider;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;

public class OtpSmsSender {
    static Logger logger = Logger.getLogger(OtpSmsSender.class);

    public static void sendSMS(KeycloakSession session, String phone, String code) {
        logger.info("Send OTP Code [" + code + "] to Phone Number [" + phone + "]");
        String realm = session.getContext().getRealm().getName();
        KafkaProvider.getInstance(realm).produce(
                MessageKafkaDto.builder()
                        .realm(realm)
                        .type(KafkaType.OTP)
                        .username(phone)
                        .code(code)
                        .build()
        );
    }
}
