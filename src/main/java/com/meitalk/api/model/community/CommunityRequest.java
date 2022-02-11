package com.meitalk.api.model.community;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class CommunityRequest {

    @Getter
    @Setter
    @ToString
    public static class CommunityUpdate {
        private String contents;
    }

}
