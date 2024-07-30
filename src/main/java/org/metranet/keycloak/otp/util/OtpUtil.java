package org.metranet.keycloak.otp.util;

import org.apache.commons.collections4.map.PassiveExpiringMap;

public class OtpUtil {
    private static PassiveExpiringMap<String, String> stock;

    public static String getCode(String key) {
        if(null == stock) {
            stock = new PassiveExpiringMap<>(2 * 60000);
        }
        return stock.get(key);
    }
    
    public static void addCode(String key, String code) {
        if(null == stock) {
            stock = new PassiveExpiringMap<>(2 * 60000);
        }
        stock.put(key, code);
    }
}
