package com.shahrtech.keycloak.otp.kafka;

import lombok.Builder;
import com.shahrtech.keycloak.otp.util.KafkaType;

import java.util.UUID;

@Builder
public record MessageKafkaDto(String id,
                              Long time,
                              KafkaType type,
                              String realm,
                              String client,
                              String username,
                              String code,
                              String ipAddress) {
    @Override
    public String id() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Long time() {
        return System.currentTimeMillis();
    }
}
