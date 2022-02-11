package com.meitalk.api.model.community;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class CommunityResponse {

    @Getter
    @Setter
    @ToString
    public static class CommunityDetail {
        private Long id;
        private Long channelId;
        private String contents;
        private String createTime;
        private String updateTime;
        private String deleteTime;
    }

}
