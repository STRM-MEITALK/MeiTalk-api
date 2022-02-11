package com.meitalk.api.model.wallet;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class ReqWallet {

    @Getter
    @Setter
    @ToString
    public static class ExternalTransaction {
        private String coinType;
        private String type;
        private String amount;
        private String sender;
        private String receiver;
        private String txid;
        private String fee;
    }

}
