package com.easylearning.api.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final String code;

    public BadRequestException(String message) {
        super(message);
        this.code = null;
    }

    public BadRequestException(String message, String code) {
        super(message);
        this.code = code;
    }
}