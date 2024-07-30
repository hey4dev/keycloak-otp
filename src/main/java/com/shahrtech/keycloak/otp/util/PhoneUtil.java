package com.shahrtech.keycloak.otp.util;

public class PhoneUtil {
    public static String correctPartOfCelPhone(String cellPhone) {
        if (cellPhone.startsWith("0"))
            cellPhone = cellPhone.substring(1);
        if (cellPhone.startsWith("98"))
            cellPhone = cellPhone.substring(2);
        if (cellPhone.startsWith("+98"))
            cellPhone = cellPhone.substring(3);
        return cellPhone;
    }

    public static boolean validateCellPhoneNumber(String cellPhone) {
        cellPhone = correctPartOfCelPhone(cellPhone);

        if (cellPhone.length() != 10)
            return false;
        try {
            Long.parseLong(cellPhone);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String refineCellPhoneNumber(String cellPhone) {
        cellPhone = correctPartOfCelPhone(cellPhone);
        return "98" + cellPhone;
    }

}
