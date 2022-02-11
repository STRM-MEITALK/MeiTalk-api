package com.meitalk.api.model.stream.enums;

import lombok.Getter;

@Getter
public enum WatchedMenuType {
    AFTER("after"),
    LIKED("liked");

    private final String value;

    WatchedMenuType(String value) {
        this.value = value;
    }
}
