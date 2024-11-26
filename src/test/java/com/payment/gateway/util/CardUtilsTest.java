package com.payment.gateway.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CardUtilsTest {

    @Test
    void isValidLuhn_ValidCardNumber_ShouldReturnTrue() {
        assertTrue(CardUtils.isValidLuhn("4111111111111111"));
    }

    @Test
    void isValidLuhn_InvalidCardNumber_ShouldReturnFalse() {
        assertFalse(CardUtils.isValidLuhn("1234567890123450"));
    }

    @Test
    void isEvenSumOfDigits_OddSum_ShouldReturnFalse() {
        assertFalse(CardUtils.isEvenSumOfDigits("123456"));
    }

    @Test
    void isEvenSumOfDigits_EvenSum_ShouldReturnFalse() {
        assertTrue(CardUtils.isEvenSumOfDigits("123457"));
    }

    @Test
    void getBin_ShouldReturnFirstSixDigits() {
        assertEquals("123456", CardUtils.getBin("1234567890123456"));
    }

    @Test
    void getLastDigit_ShouldReturnLastDigit() {
        assertEquals('6', CardUtils.getLastDigit("1234567890123456"));
    }
}