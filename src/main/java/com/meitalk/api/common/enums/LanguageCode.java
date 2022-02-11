package com.meitalk.api.common.enums;

import lombok.Getter;

@Getter
public enum LanguageCode {

    JA("ja", "-JA.json"),
    EN("en", "-EN.json"),
    RU("ru", "-RU.json"),
    KO("ko", "-KO.json"),
    ZH("zh", "-ZH.json"),
    ID("id", "-ID.json");

    private final String value;
    private final String extensions;

    LanguageCode(String value, String extensions) {
        this.value = value;
        this.extensions = extensions;
    }
}
