package com.tutormarketplace.pattern.command;

public class CommandExecutionException extends Exception {
    public CommandExecutionException(String message) {
        super(message);
    }
    
    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
