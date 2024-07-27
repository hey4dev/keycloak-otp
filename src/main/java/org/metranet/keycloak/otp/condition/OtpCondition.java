package org.metranet.keycloak.otp.condition;

import jakarta.ws.rs.core.MultivaluedMap;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.conditional.ConditionalAuthenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class OtpCondition implements ConditionalAuthenticator {
    Logger logger = Logger.getLogger(OtpCondition.class);

    @Override
    public boolean matchCondition(AuthenticationFlowContext authenticationFlowContext) {
        try {
            MultivaluedMap<String, String> decodedFormParameters = authenticationFlowContext.getHttpRequest().getDecodedFormParameters();
            logger.info(decodedFormParameters);
            String type = decodedFormParameters.get("otp-code").get(0);
            logger.info(type);
            return type == null;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return true;
        }
    }

    @Override
    public void action(AuthenticationFlowContext authenticationFlowContext) {

    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {

    }

    @Override
    public void close() {

    }
}
