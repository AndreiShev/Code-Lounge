package ru.skillbox.exception;

public class JwtParseException extends RuntimeException {
    
    public JwtParseException(String message) {
        super(message);
    }
}
