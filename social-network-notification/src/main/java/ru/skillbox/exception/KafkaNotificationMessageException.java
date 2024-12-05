package ru.skillbox.exception;

public class KafkaNotificationMessageException extends RuntimeException {

    public KafkaNotificationMessageException(String message) {
        super(message);
    }
}
