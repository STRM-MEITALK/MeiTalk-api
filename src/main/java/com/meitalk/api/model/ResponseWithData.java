package com.meitalk.api.model;

import com.meitalk.api.exception.ResponseCodeEnum;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ResponseWithData {

    private ResponseBuilder response;
    private Object data;

    @Builder
    public ResponseWithData(ResponseBuilder response, Object data) {
        this.response = response;
        this.data = data;
    }

    public static ResponseWithData success() {
        return success(null);
    }

    public static ResponseWithData success(Object data) {
        return process(ResponseCodeEnum.SUCCESS, data);
    }

    public static ResponseWithData failed(ResponseCodeEnum responseCodeEnum) {
        return process(responseCodeEnum, null);
    }

    public static ResponseWithData process(ResponseCodeEnum responseCodeEnum, Object data) {
        return ResponseWithData.builder()
                .response(ResponseBuilder.builder()
                        .output(responseCodeEnum.getCode())
                        .result(responseCodeEnum.getMessage())
                        .build())
                .data(data)
                .build();
    }

    public ResponseWithData message(String message) {
        this.response.setResult(message);
        return this;
    }
}
