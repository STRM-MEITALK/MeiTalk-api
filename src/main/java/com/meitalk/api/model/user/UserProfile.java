package com.meitalk.api.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserProfile {

    private int userNo;
    private String mailId;
    private String userName;
    private String userPw;
    private String countryPhone;
    private String phoneNum;
    private String ipAddr;
    private String userCreateTime;
    private String updateTime;
    private String role;
    private String block;
    private String platform;
    private String privacyAgree;
    private String userPicture;
    private String oauth2Key;
    private String emailVerification;

}
