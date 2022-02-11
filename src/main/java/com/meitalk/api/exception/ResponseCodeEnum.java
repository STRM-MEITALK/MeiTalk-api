package com.meitalk.api.exception;

public enum ResponseCodeEnum {

    DATABASE_ERROR(-101, "DATABASE_ERROR"),
    SUCCESS(0,"SUCCESS"),
    FAILED(-1, "FAILED"),
    UNKNOWN_ERROR(-100, "UNKNOWN_ERROR"),
    WRONG_TYPE_TOKEN(-99, "WRONG_TYPE_TOKEN"),
    EXPIRED_TOKEN(-88, "EXPIRED_TOKEN"),
    UNSUPPORTED_TOKEN(-77, "UNSUPPORTED_TOKEN"),
    WRONG_TOKEN(-66, "WRONG_TOKEN"),
    ACCESS_DENIED(-44, "ACCESS_DENIED"),
    QUERY_FAILED(-33,"QUERY_FAILED"),
    INVALID_PARAMETER(-22,"INVALID_PARAMETER"),
    RESULT_NOT_FOUND(-11,"RESULT_NOT_FOUND");



    private final int code;
    private final String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ResponseCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
