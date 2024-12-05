package ru.skillbox.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.skillbox.dto.ErrorResponse;
import ru.skillbox.exception.AccessResourseException;
import ru.skillbox.exception.BadArgumentException;

@RestControllerAdvice
public class ExceptionHandlerController {

  @ExceptionHandler(AccessResourseException.class)
  @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
  public ErrorResponse accessFailed (AccessResourseException e) {
    return new ErrorResponse(e.getLocalizedMessage());
  }

  @ExceptionHandler(BadArgumentException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ErrorResponse badRequest (BadArgumentException e) {
    return new ErrorResponse(e.getLocalizedMessage());
  }
}
