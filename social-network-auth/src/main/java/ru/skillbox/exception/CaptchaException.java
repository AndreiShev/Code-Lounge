package ru.skillbox.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CaptchaException extends RuntimeException {
    public CaptchaException(String message) {
        super(message);
    }
}
