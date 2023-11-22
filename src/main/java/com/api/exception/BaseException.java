package com.api.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
public class BaseException extends RuntimeException {
    @JsonProperty("code")
    private String code;
    @JsonProperty("message")
    private String message;
    @JsonIgnore
    private String cause;
    @JsonIgnore
    private Object stackTrace;
    @JsonIgnore
    private List<String> suppressed;
    @JsonIgnore
    private String localizedMessage;
    public BaseException(String code, String message){
        this.code = code;
        this.message = message;
    }
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BaseException{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
