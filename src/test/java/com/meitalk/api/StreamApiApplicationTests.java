package com.meitalk.api;

import com.meitalk.api.model.ResponseWithData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static com.meitalk.api.exception.ResponseCodeEnum.EXPIRED_TOKEN;

@Slf4j
class StreamApiApplicationTests {

    @Test
    void contextLoads() {
        int i = 5;
        System.out.println("result -> " + ResponseWithData.success());
        System.out.println("result -> " + ResponseWithData.success("asd"));
        System.out.println("result -> " + ResponseWithData.success().message("메시지만"));
        System.out.println("result -> " + ResponseWithData.success(i).message("메시지 변경!!"));

        System.out.println("result -> " + ResponseWithData.failed(EXPIRED_TOKEN));
        System.out.println("result -> " + ResponseWithData.failed(EXPIRED_TOKEN).message("토큰 만료다."));
        System.out.println("start");
    }

}
