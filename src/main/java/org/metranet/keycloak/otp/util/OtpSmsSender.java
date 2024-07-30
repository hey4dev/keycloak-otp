package org.metranet.keycloak.otp.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.metranet.keycloak.otp.kafka.MessageKafkaDto;
import org.metranet.keycloak.otp.provider.KafkaProvider;

import java.io.IOException;

import static org.metranet.keycloak.otp.util.OtpSmsConstant.getContent;

public class OtpSmsSender {
    static Logger logger = Logger.getLogger(OtpSmsSender.class);
    static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static void sendSMS(KeycloakSession session, String phone, String code) {
        logger.info("Send OTP Code [" + code + "] to Phone Number [" + phone + "]");

        KafkaProvider.getInstance().produce(
                MessageKafkaDto.builder()
                        .realm(session.getContext().getRealm().getName())
                        .type(KafkaType.OTP)
                        .username(phone)
                        .code(code)
                        .build()
        );

        //        String token = getToken();
//        logger.info(token);
//
//        if (token == null)
//            throw new InternalServerErrorException("token is null");
//
//        HttpPost httpPost = new HttpPost("https://smsapi.asiatech.ir/api/2/message/send");
//
//        // Set headers
//        httpPost.setHeader("Accept", "application/json");
//        httpPost.setHeader("Content-Type", "application/json");
//        httpPost.setHeader("Authorization", "Bearer " + token.trim());
//
//        String msg = smsMessageBuilder(code);
//        msg = objectMapper.writeValueAsString(msg);
//        String params = "[{\"sourceAddress\":\"9890002734\",\"MessageText\":" + msg + ",\"destinationAddress\":\"" + phone + "\"}]";
//
//        // Set entity
//        httpPost.setEntity(new StringEntity(params, ContentType.APPLICATION_JSON.withCharset("UTF-8")));
//        httpClient.execute(httpPost);
    }

    private static String getToken() throws IOException {
        HttpPost httpPost = new HttpPost("https://smsapi.asiatech.ir/connect/token");

        // Set headers
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

        // Build form parameters
        StringBuilder params = new StringBuilder();
        params.append("scope=").append("ApiAccess").append("&");
        params.append("username=").append("rahbord").append("&");
        params.append("password=").append("Rah@9000");

        // Set entity
        httpPost.setEntity(new StringEntity(params.toString()));
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(getContent(entity.getContent()));
            String access_token = jsonNode.get("access_token").toString();
            if (access_token != null)
                return access_token.replace("\"", "").trim();
        }

        return null;
    }

    private static String smsMessageBuilder(String code) {
        return "اسمارتیز" +
                System.getProperty("line.separator") +
                System.getProperty("line.separator") +
                " code : " + code +
                System.getProperty("line.separator") +
                "کاربر عزیز، این کد جهت ورود به اپلیکیشن است. لطفا آن را در اختیار شخص دیگری قرار ندهید." +
                System.getProperty("line.separator") +
                " کد احراز هویت شما : " + code +
                System.getProperty("line.separator") +
                "@appsit.shahrtech.com #" + code;
    }

}
