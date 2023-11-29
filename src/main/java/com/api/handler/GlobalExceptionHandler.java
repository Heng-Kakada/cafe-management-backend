package com.api.handler;

import com.api.dto.response.AuthResponse;
import com.api.dto.response.ResponseErrorTemplate;
import com.api.exception.BaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> baseExceptionHandler(BaseException e){
        return ResponseEntity.ok().body(new ResponseErrorTemplate(e.getCode(), e.getMessage(), null));
    }

}
