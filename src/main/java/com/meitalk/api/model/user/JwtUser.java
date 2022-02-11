package com.meitalk.api.model.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class JwtUser {
    private Long userNo;
    private String email;
    private String name;
    private String role;

    @Builder
    public JwtUser(Long userNo, String email, String name, String role) {
        this.userNo = userNo;
        this.email = email;
        this.name = name;
        this.role = role;
    }
}
