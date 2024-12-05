package ru.skillbox.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.skillbox.dto.error.Error;
import ru.skillbox.dto.error.ErrorResponse;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> notValidMethodArgument(MethodArgumentNotValidException ex) {

        List<String> errorMessage = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> errorMessage.add(error.getDefaultMessage()));


        List<Error> errorResponse = new ArrayList<>();
        for (String message : errorMessage) {
            errorResponse.add(new Error(message, Arrays.toString(ex.getStackTrace())));
        }

        return new ResponseEntity<>(new ErrorResponse(errorResponse), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> notSupportedHttpMethod(HttpRequestMethodNotSupportedException ex) {
        String supportedMethods = String.join(", ", ex.getSupportedMethods());
        Error error = new Error(
                MessageFormat.format("Метод {0} не поддреживается", ex.getMethod()),
                MessageFormat.format("Метод должен быть {0}", supportedMethods)
        );

        return new ResponseEntity<>(new ErrorResponse(List.of(error)), HttpStatus.METHOD_NOT_ALLOWED);
    }
}





