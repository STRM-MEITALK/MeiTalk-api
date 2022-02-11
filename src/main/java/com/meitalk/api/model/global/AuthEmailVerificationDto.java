package com.meitalk.api.model.global;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthEmailVerificationDto {

    private int userId;
    private String userKey;

    @Builder
    public AuthEmailVerificationDto(int userId, String userKey) {
        this.userId = userId;
        this.userKey = userKey;
    }
}
