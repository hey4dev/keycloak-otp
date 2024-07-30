package com.shahrtech.keycloak.otp.factory;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import com.shahrtech.keycloak.otp.provider.OtpSmsFormAuthenticator;

import java.util.List;

public class OtpSmsFormAuthenticatorFactory implements AuthenticatorFactory {

    public static final String ID = "otp-sms-form";

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
        AuthenticationExecutionModel.Requirement.REQUIRED,
        AuthenticationExecutionModel.Requirement.CONDITIONAL,
        AuthenticationExecutionModel.Requirement.DISABLED
    };

    @Override
    public Authenticator create(KeycloakSession session) {
        return new OtpSmsFormAuthenticator(session);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getReferenceCategory() {
        return "otp-sms";
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public String getDisplayType() {
        return "OTP Using SMS";
    }

    @Override
    public String getHelpText() {
        return "OTP Using SMS";
    }
    
    @Override
    public boolean isConfigurable() {
        return false;
    }
    
    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }
    
    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }
    
    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // do nothing
    }

    @Override
    public void init(Config.Scope config) {
        // do nothing
    }
    
    @Override
    public void close() {
        // do nothing
    }

}
