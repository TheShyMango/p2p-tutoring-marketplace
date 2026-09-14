package com.tutormarketplace.pattern.state;

public class InvalidStateTransitionException extends Exception {
    public InvalidStateTransitionException(String message) {
        super(message);
    }
}
