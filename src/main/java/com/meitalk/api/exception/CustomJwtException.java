package com.meitalk.api.exception;

public class CustomJwtException extends CustomException {

    public CustomJwtException(String message) {
        super(message);
    }

    public CustomJwtException(ResponseCodeEnum code) {
        super(code);
    }

    public CustomJwtException(ResponseCodeEnum code, String message) {
        super(code, message);
    }
}