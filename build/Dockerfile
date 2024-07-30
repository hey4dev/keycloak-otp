FROM quay.io/keycloak/keycloak:25.0.2

COPY ./themes/otpsms/ /opt/keycloak/themes/otpsms/
COPY otp-sms-3.4.0.jar /opt/keycloak/providers/otp-sms-3.4.0.jar
# COPY ./keycloak-kafka-1.1.5-jar-with-dependencies.jar /opt/keycloak/providers/keycloak-kafka-1.1.5-jar-with-dependencies.jar
RUN cd /opt/keycloak/providers && /opt/keycloak/bin/kc.sh build