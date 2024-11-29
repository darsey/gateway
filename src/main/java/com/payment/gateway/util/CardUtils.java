package com.payment.gateway.util;

public class CardUtils {

    private CardUtils() {
    }

    public static boolean isValidLuhn(String cardNumber) {
        if (!cardNumber.matches("(?=[456]|37)[0-9]{13,16}")) {
            return false;
        }
        int sum = 0;
        for (int i = cardNumber.length() - 1, pos = 1; i >= 0; i--, pos++) {
            int digit = cardNumber.charAt(i) - '0';
            sum += (pos % 2 == 1 ? digit : digit < 5 ? digit * 2 : digit * 2 - 9);
        }
        return (sum % 10 == 0);
    }

    public static boolean isEvenSumOfDigits(String bin) {
        int sum = bin.chars().map(Character::getNumericValue).sum();
        return sum % 2 == 0;
    }

    public static String getBin(String cardNumber) {
        return cardNumber.substring(0, 6);
    }

    public static char getLastDigit(String cardNumber) {
        return cardNumber.charAt(cardNumber.length() - 1);
    }
}
