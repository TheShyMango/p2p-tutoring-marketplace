package com.tutormarketplace.pattern.facade;

public class SessionSettlementException extends Exception {
    public SessionSettlementException(String message) {
        super(message);
    }
    
    public SessionSettlementException(String message, Throwable cause) {
        super(message, cause);
    }
}
