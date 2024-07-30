package com.shahrtech.keycloak.otp.api;

import com.shahrtech.keycloak.otp.util.*;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;

public class RequestOtpController {
    Logger logger = Logger.getLogger(RequestOtpController.class);
    private final KeycloakSession session;

    public RequestOtpController(KeycloakSession session) {
        this.session = session;
    }

    private String getRandomDigit() {
        RandomStringUtil random = new RandomStringUtil(4, RandomStringUtil.NUMERIC);
        return random.nextString();
    }

    @GET
    @Path("ck/{k}/{c}")
    public Response verifyOtp(@PathParam("k") String key, @PathParam("c") String code) {
        if (null != code && !code.isEmpty() && null != key && !key.isEmpty()) {
            String codesession = OtpUtil.getCode(OtpSmsConstant.SESSION_OTP_CODE + key);
            if (code.equals(codesession)) {
                return Response.ok(true).build();
            }
        }
        return Response.ok(false).build();
    }

    @GET
    @Path("{m}")
    public Response requestOtp(@PathParam("m") String mobileNumber) {
        try {
            mobileNumber = PhoneUtil.refineCellPhoneNumber(mobileNumber);
            // Generate Random Digit
            String key = getRandomDigit();

            // Put the data into session, to be compared
            String flag = OtpSmsConstant.SESSION_OTP_CODE + mobileNumber;
            OtpUtil.addCode(flag, key);

            // Send SMS
            OtpSmsSender.sendSMS(session, mobileNumber, key);

            return Response.ok(true).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Response.ok(false).build();
        }
    }
}
