package com.meitalk.api.exception;

public class StreamException extends CustomException {

    public StreamException(String message) {
        super(message);
    }

    public StreamException(ResponseCodeEnum code) {
        super(code);
    }

    public StreamException(ResponseCodeEnum code, String message) {
        super(code, message);
    }
}
