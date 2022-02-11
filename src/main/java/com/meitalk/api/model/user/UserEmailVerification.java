package com.meitalk.api.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserEmailVerification {

    private int verificationId;
    private String verificationUserNo;
    private String userKey;
    private String verification;
    private String verificationCreateTime;

}
