package com.meitalk.api.model.community;

import lombok.*;

import java.util.List;

public class CommunityDto {

    @Getter
    @NoArgsConstructor
    public static class Request {
        private Long channelId;
        private String contents;
    }

    @Getter
    @Setter
    @ToString
    @Builder
    public static class CreateCommunityBoard{
        private Long id;
        private Long channelId;
        private String contents;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CommunityList {
        private Long id;
        private Long channelId;
        private String contents;
        private String createTime;
        private String updateTime;
        private Long commentCount;
    }

    @Data
    @Builder
    public static class CommunityListWrapper {
        List<CommunityList> communityList;
        private Integer pageNo;
        private Integer pageSize;
        private Integer totalElements;
    }
}
