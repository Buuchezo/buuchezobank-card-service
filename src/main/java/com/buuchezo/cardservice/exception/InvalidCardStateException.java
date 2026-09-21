package com.buuchezo.cardservice.exception;

public class InvalidCardStateException extends RuntimeException {

    public InvalidCardStateException(String message) {
        super(message);
    }
}
