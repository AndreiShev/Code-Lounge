package ru.skillbox.exception;

public class InvalidUserIdFormatException extends RuntimeException {
    public InvalidUserIdFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}

