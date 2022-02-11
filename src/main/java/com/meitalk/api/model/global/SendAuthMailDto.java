package com.meitalk.api.model.global;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SendAuthMailDto {

    private String userMail;
    private String userKey;

    @Builder
    public SendAuthMailDto(String userMail, String userKey) {
        this.userMail = userMail;
        this.userKey = userKey;
    }
}
