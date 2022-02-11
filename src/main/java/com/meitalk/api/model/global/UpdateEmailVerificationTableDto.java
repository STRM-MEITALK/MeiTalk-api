package com.meitalk.api.model.global;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateEmailVerificationTableDto {

    private int userNo;
    private String verification;

    @Builder
    public UpdateEmailVerificationTableDto(int userNo, String verification) {
        this.userNo = userNo;
        this.verification = verification;
    }
}
