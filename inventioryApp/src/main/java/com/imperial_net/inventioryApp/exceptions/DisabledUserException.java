package com.imperial_net.inventioryApp.exceptions;

public class DisabledUserException extends RuntimeException {
    public DisabledUserException(String message) {
        super(message);
    }
}
