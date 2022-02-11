package com.meitalk.api.model.wallet;

import lombok.Getter;

@Getter
public enum CoinTypeEnum {
    BNB("BNB"),
    STRM("STRM"),
    ABBC("ABBC");

    private final String coinName;

    CoinTypeEnum(String coinName) {
        this.coinName = coinName;
    }
}
