package ru.skillbox.exception;

public class IllegalFriendRequestException extends RuntimeException {
    public IllegalFriendRequestException(String message) {
        super(message);
    }
}
