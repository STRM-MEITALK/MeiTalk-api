package com.meitalk.api.common;

import com.meitalk.api.exception.CustomException;
import com.meitalk.api.exception.CustomJwtException;
import com.meitalk.api.exception.StreamException;
import com.meitalk.api.model.ResponseWithData;
import com.amazonaws.services.ivs.model.ChannelNotBroadcastingException;
import com.meitalk.api.exception.ResponseCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            StreamException.class,
            CustomJwtException.class
    })
    public ResponseWithData customException(CustomException e) {
        log.error("{} : {}", e.getClass(), e.getMessage());
        log.error("{}", e.getStackTrace()[0]);
        return ResponseWithData.failed(e.getResponseCodeEnum()).message(e.getMessage());
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            BindException.class,
            ChannelNotBroadcastingException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseWithData handlingException(Exception e) {
        log.error("{} : {}", e.getClass(), e.getMessage());
        log.error("{}", e.getStackTrace()[0]);
        return ResponseWithData.failed(ResponseCodeEnum.FAILED).message(e.getMessage());
    }
}
