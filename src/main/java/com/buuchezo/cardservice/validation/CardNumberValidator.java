package com.buuchezo.cardservice.validation;

import org.springframework.stereotype.Component;

@Component
public class CardNumberValidator {

    public boolean isValid(String cardNumber) {

        if (cardNumber == null || cardNumber.isBlank()) {
            return false;
        }

        String normalized = cardNumber.replaceAll("\\s+", "");

        if (!normalized.matches("\\d{16,19}")) {
            return false;
        }

        int sum = 0;
        boolean doubleDigit = false;

        for (int i = normalized.length() - 1; i >= 0; i--) {

            int digit = Character.digit(
                    normalized.charAt(i),
                    10
            );

            if (doubleDigit) {
                digit *= 2;

                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            doubleDigit = !doubleDigit;
        }

        return sum % 10 == 0;
    }
}
