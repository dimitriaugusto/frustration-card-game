package com.dimilo.frustration.utils;

public class StringUtils {

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static String posIntToString(int intValue) {
        return intValue > 0 ? Integer.toString(intValue) : "";
    }

    public static int stringToInt(String stringValue) {
        return Integer.parseInt(stringValue);
    }

}