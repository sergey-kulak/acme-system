package com.acme.usersrv.test;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomTestUtils {
    private RandomTestUtils() {
    }

    public static String randomString(String suffix) {
        return randomString(suffix, 5);
    }

    public static String randomString(String suffix, int randomLength) {
        return suffix + RandomStringUtils.randomNumeric(randomLength);
    }

    public static String randomPhoneNumber() {
        return "+358" + RandomStringUtils.randomNumeric(9);
    }

    public static String randomEmail() {
        return randomString("email", 5) + "@mail.com";
    }
}
