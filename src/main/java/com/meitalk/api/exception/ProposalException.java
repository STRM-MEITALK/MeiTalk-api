package com.meitalk.api.exception;

public class ProposalException extends CustomException{
    public ProposalException(String message) {
        super(message);
    }

    public ProposalException(ResponseCodeEnum code) {
        super(code);
    }

    public ProposalException(ResponseCodeEnum code, String message) {
        super(code, message);
    }
}
