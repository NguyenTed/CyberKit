package com.cyberkit.cyberkit_server.exception;


import com.cyberkit.cyberkit_server.dto.response.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.Field;
import java.util.List;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> handelArgumentValidationError(MethodArgumentNotValidException exception){
        RestResponse res = new RestResponse<>();
        res.setMessage(exception.getBody().getDetail());

        final List<FieldError> fieldErrorList=  exception.getBindingResult().getFieldErrors();
        List<String> errors = fieldErrorList.stream().map(f -> f.getDefaultMessage()).toList();
        res.setMessage(errors.size()>1? errors: errors.get(0));
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(value ={
            UsernameNotFoundException.class,
            BadCredentialsException.class
    })
    public ResponseEntity<RestResponse<Object>> handleException(Exception exception){
        RestResponse res = new RestResponse<>();
        res.setError(exception.getMessage());
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage("Exception occurs");
        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(value = GeneralAllException.class)
    public  ResponseEntity<RestResponse<Object>> handleGeneralException(GeneralAllException exception){
        RestResponse res = new RestResponse<>();
        res.setError(exception.getMessage());
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(exception.getMessage());
        return ResponseEntity.badRequest().body(res);
    }

}
