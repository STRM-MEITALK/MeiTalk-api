package com.meitalk.api.model.wallet;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class DtoWallet {

    @Getter
    @Setter
    @ToString
    @Builder
    public static class CreateWalletRequestDto {
        private String email;
        private String password;
    }

    @Getter
    @Setter
    @ToString
    public static class CreateWalletResponseDto {
        private String success;
        private CreateWalletResponseDataDto data;
    }

    @Getter
    @Setter
    @ToString
    public static class CreateWalletResponseDataDto {
        private String id;
        private String email;
        private String encryptedPassword;
        private String role;
        private String BTC;
        private String ETH;
        private String BTC_Balance;
        private String ETH_Balance;
        private String pk;
        private String verificationKey;
        private String isVerified;
        private String updatedAt;
        private String createdAt;
    }

    @Getter
    @Setter
    @ToString
    @Builder
    public static class CreateWalletDto {
        private Long userNo;
        private String userEmail;
        private String BTC;
        private String ETH;
        private String BNB;
        private String STRM;
        private String ABBC;
    }

    @Getter
    @Setter
    @ToString
    @Builder
    public static class UserAmount {
        private Long userNo;
        private String userEmail;
        private String BTC;
        private String ETH;
        private String BNB;
        private String STRM;
        private String ABBC;
    }
}