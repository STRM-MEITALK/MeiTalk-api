package com.meitalk.api.model.stream.enums;

import lombok.Getter;

@Getter
public enum RedisKey {

    USER_COUNT("string"),
    MAIN_STREAM_LIST("hash"),
    USER_CHANNEL_STREAM_LIST("hash"),
    MY_STUDIO_STREAM_LIST("hash");

    private String type;

    RedisKey(String type) {
        this.type = type;
    }
}
