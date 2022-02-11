package com.meitalk.api.model.stream.enums;

import lombok.Getter;

@Getter
public enum TransmittedType {

    INTERNAL("I"),
    EXTERNAL("E");

    private final String value;

    TransmittedType(String value) {
        this.value = value;
    }
}
