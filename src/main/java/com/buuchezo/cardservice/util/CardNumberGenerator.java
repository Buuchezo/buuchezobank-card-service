package com.buuchezo.cardservice.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CardNumberGenerator {

    private static final int CARD_NUMBER_LENGTH = 16;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateCardNumber() {

        StringBuilder cardNumber = new StringBuilder(
                CARD_NUMBER_LENGTH
        );

        // Start with 4 for a Visa-like test card.
        cardNumber.append(4);

        // Generate the remaining 14 digits.
        for (int i = 1; i < CARD_NUMBER_LENGTH - 1; i++) {
            cardNumber.append(
                    secureRandom.nextInt(10)
            );
        }

        // Add the Luhn check digit.
        cardNumber.append(
                calculateCheckDigit(cardNumber.toString())
        );

        return cardNumber.toString();
    }

    private int calculateCheckDigit(String numberWithoutCheckDigit) {

        int sum = 0;
        boolean doubleDigit = true;

        for (int i = numberWithoutCheckDigit.length() - 1;
             i >= 0;
             i--) {

            int digit = Character.digit(
                    numberWithoutCheckDigit.charAt(i),
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

        return (10 - (sum % 10)) % 10;
    }
}
