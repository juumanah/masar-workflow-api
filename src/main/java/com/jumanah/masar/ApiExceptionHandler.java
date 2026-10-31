package com.jumanah.masar;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//Handles exceptions centrally for our RESTT controllers
@RestControllerAdvice
public class ApiExceptionHandler {

    //Handles input that failled validation such as @NotBlank
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new LinkedHashMap<>();

        //associate each invalid field with its validation message
        for (var error : exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        return Map.of(
                "status", 400,
                "message", "Validation failed",
                "errors", errors
        );
    }

    //it willreturn 409 when another transaction changed the same req
@ExceptionHandler(OptimisticLockingFailureException.class)
@ResponseStatus(HttpStatus.CONFLICT)
public Map<String, Object> handleConcurrentUpdate(
        OptimisticLockingFailureException exception) {

    return Map.of(
            "status", 409,
            "message", "This request was changed by another operation. Reload it before trying again."
    );
}
}