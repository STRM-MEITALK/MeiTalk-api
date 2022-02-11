package com.meitalk.api.model.stream.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum StreamType {
    LIVE("Y"),
    VOD("N"),
    ALL("A"),
    HOT("H"),
    WEEKLY("W"),
    MONTHLY("M");

    private final String value;

    StreamType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static StreamType from(String s) {
        return StreamType.valueOf(s.toUpperCase());
    }
}
