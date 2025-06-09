package com.easylearning.api.utils;

import org.apache.commons.lang.RandomStringUtils;

import java.text.DecimalFormat;

public class StringUtils {
    public static String generateRandomString(int length) {
        return RandomStringUtils.randomAlphanumeric(length).toLowerCase();
    }
    public static Double roundNumber(Double result, int length) {
        DecimalFormat df = new DecimalFormat("#." + "#".repeat(Math.max(0, length)));
        return Double.valueOf(df.format(result));
    }
}
