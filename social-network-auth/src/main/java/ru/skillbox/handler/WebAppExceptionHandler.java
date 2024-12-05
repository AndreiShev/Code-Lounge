package ru.skillbox.handler;


import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.skillbox.dto.responses.ErrorResponse;
import ru.skillbox.exception.AlreadyExistsException;
import ru.skillbox.exception.CaptchaException;
import ru.skillbox.exception.EntityNotFoundException;
import ru.skillbox.exception.RefreshTokenException;

import java.util.List;

@RestControllerAdvice
public class WebAppExceptionHandler {
    @ExceptionHandler(value = CaptchaException.class)
    public ResponseEntity<ErrorResponse> runtimeExceptionHandler(CaptchaException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(value = FeignException.class)
    public ResponseEntity<ErrorResponse> feignExceptionHandler(FeignException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(value = RefreshTokenException.class)
    public ResponseEntity<ErrorResponse> refreshTokenExceptionHandler(RefreshTokenException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(value = AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> alreadyExistHandler(AlreadyExistsException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundHandler(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> notValid(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errorMessages = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        String errorMessage = String.join("; ", errorMessages);
        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> notValid(ConstraintViolationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
    }


    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> alreadyExistHandler(BadCredentialsException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus)
                .body(ErrorResponse.builder()
                        .errorDescription(message)
                        .build());
    }
}
