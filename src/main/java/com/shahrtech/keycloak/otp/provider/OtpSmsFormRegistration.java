package com.shahrtech.keycloak.otp.provider;

import jakarta.ws.rs.core.MultivaluedMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormActionFactory;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.*;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.provider.ProviderConfigProperty;
import com.shahrtech.keycloak.otp.util.OtpSmsConstant;

import java.util.ArrayList;
import java.util.List;

import static com.shahrtech.keycloak.otp.util.OtpSmsConstant.getContent;


public class OtpSmsFormRegistration implements FormAction, FormActionFactory {
    public static Logger logger = Logger.getLogger(OtpSmsFormRegistration.class);

    public static final String ID = "otp-sms-form-registration";

    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.CONDITIONAL,
            AuthenticationExecutionModel.Requirement.DISABLED
    };

    KeycloakSession session;

    @Override
    public FormAction create(KeycloakSession session) {
        this.session = session;
        return this;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getReferenceCategory() {
        return "otp-sms-registration";
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public String getDisplayType() {
        return "OTP Using SMS Registration";
    }

    @Override
    public String getHelpText() {
        return "OTP Using SMS Registration";
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

    @Override
    public void buildPage(FormContext context, LoginFormsProvider form) {
        // do nothing
    }

    private String getMobileNumber(ValidationContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String number = formData.getFirst(OtpSmsConstant.ATTR_PHONE_NUMBER);
        if (null == number) {
            return "";
        }
        return number;
    }

    private String getCodeOtp(ValidationContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String otpcode = formData.getFirst(OtpSmsConstant.ATTR_OTP_CODE);
        if (null == otpcode) {
            return "";
        }
        return otpcode;
    }


    public static String validity(KeycloakSession session, String realm, String mobileNumber, String code) {
        HttpClient httpClient = session.getProvider(HttpClientProvider.class).getHttpClient();
        HttpGet get = new HttpGet(OtpSmsConstant.HTTP_AUTH_HOST + "/realms/" + realm + "/otpsms/ck/" + mobileNumber + "/" + code);
        try {
            HttpResponse response = httpClient.execute(get);
            return getContent(response.getEntity().getContent());
        } catch (Exception e) {
            logger.error("Error", e);
        }
        return "";
    }

    @Override
    public void validate(ValidationContext context) {
        String mobileNumber = getMobileNumber(context);
        String codeOtp = getCodeOtp(context);
        if ("true".equals(validity(context.getSession(), context.getRealm().getName(), mobileNumber, codeOtp))) {
            context.success();
            return;
        }

        List<FormMessage> errors = new ArrayList<>();
        errors.add(new FormMessage("user.attributes.otp", "OTP Code is invalid</br>"));

        context.error(Errors.INVALID_REGISTRATION);
        context.validationError(context.getHttpRequest().getDecodedFormParameters(), errors);
        return;
    }

    @Override
    public void success(FormContext context) {
        // do nothing
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // do nothing
    }
}
