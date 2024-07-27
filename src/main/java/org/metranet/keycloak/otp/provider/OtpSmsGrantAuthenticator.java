package org.metranet.keycloak.otp.provider;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.UTF8StreamJsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.AuthenticationFailedException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.metranet.keycloak.otp.util.OtpSmsConstant;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.metranet.keycloak.otp.provider.OtpSmsFormRegistration.validity;
import static org.metranet.keycloak.otp.util.OtpSmsConstant.getContent;
import static org.metranet.keycloak.otp.util.OtpSmsConstant.getUserByMobileNumber;

/**
 * OtpSmsFormAuthenticator digunakan untuk override Login Action dan Authentication Process.
 *
 * @author rio.bastian
 * @see AbstractUsernameFormAuthenticator
 */
public class OtpSmsGrantAuthenticator implements Authenticator {
    private final Logger logger = Logger.getLogger(OtpSmsGrantAuthenticator.class);

    @Override
    public void action(AuthenticationFlowContext context) {
        authenticate(context);
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        try {
            String sessionKey = context.getHttpRequest().getDecodedFormParameters().getFirst("otp-code");
            logger.info(sessionKey);
            String username = context.getHttpRequest().getDecodedFormParameters().getFirst("username");
            logger.info(username);
            if (sessionKey != null) {
                // Get OTP from User Input
                String otp = validity(context.getSession(), username, sessionKey);
                logger.info("otp is: " + otp);
                // Validate OTP
                if (otp != null) {
                    if (otp.equals("true")) {
                        UserModel user = getUserByMobileNumber(context, username);
                        if (user == null) {
                            logger.info("user with username: " + username + " creating...");
                            createUser(context.getSession(), context.getRealm().getName(), username);
                            logger.info("user with username: " + username + " created.");
                        }
                        context.success();
                    } else {
                        throw new AuthenticationFailedException("OTP fail");
                    }
                } else {
                    throw new AuthenticationFailedException("OTP validation fail");
                }
            } else {
                throw new AuthenticationFailedException("OTP not set");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            Response response = Response
                    .status(401)
                    .entity("{\"error\": \"invalid_grant\", \"error_description\": \"Invalid user credentials\"}")
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
            context.failure(AuthenticationFlowError.INVALID_CREDENTIALS, response);
        }
    }

    public String getToken(KeycloakSession session, String realm) throws IOException {
        HttpClient httpClient = session.getProvider(HttpClientProvider.class).getHttpClient();
        HttpPost httpPost = new HttpPost(OtpSmsConstant.HTTP_AUTH_HOST + "/realms/" + realm + "/protocol/openid-connect/token");

        // Set headers
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

        // Build form parameters
        StringBuilder params = new StringBuilder();
        params.append("client_id=").append("test").append("&");
        params.append("username=").append("server").append("&");
        params.append("password=").append("server").append("&");
        params.append("grant_type=password");

        // Set entity
        httpPost.setEntity(new StringEntity(params.toString()));
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(getContent(entity.getContent()));
            logger.info(jsonNode);
            String access_token = jsonNode.get("access_token").toString();
            if (access_token != null)
                return access_token.replace("\"", "").trim();
        }

        return null;
    }

    private void createUser(KeycloakSession session, String realm, String username) throws IOException {
        String token = getToken(session, realm);
        logger.info(token);

        if (token == null)
            throw new InternalServerErrorException("token is null");

        HttpClient httpClient = session.getProvider(HttpClientProvider.class).getHttpClient();
        HttpPost httpPost = new HttpPost(OtpSmsConstant.HTTP_AUTH_HOST + "/admin/realms/" + realm + "/users");

        // Set headers
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + token.trim());

        logger.info("Bearer " + token.trim());

        // Build form parameters
        String params = "{\"username\":\"" + username + "\",\"enabled\":true,\"emailVerified\":true,\"access\":{\"manageGroupMembership\":true,\"view\":true,\"mapRoles\":true,\"impersonate\":true,\"manage\":true}}";

        // Set entity
        httpPost.setEntity(new StringEntity(params));
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        logger.info(getContent(entity.getContent()));
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
    }

    @Override
    public void close() {
    }

}
