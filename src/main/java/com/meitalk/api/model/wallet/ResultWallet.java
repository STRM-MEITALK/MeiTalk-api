package com.meitalk.api.model.wallet;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class ResultWallet {

    @Getter
    @Setter
    @ToString
    public static class WalletUserInfo {
        private Long id;
        private Long userNo;
        private String userEmail;
        private String BNB;
        private String STRM;
        private String ABBC;
        private String createTime;
    }

}
