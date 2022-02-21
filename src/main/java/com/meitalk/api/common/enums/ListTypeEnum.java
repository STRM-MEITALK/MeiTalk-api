package com.meitalk.api.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ListTypeEnum {
    ALL("A"),
    HOT("H"),
    NEW("N")
    ;

    private final String listType;
}
