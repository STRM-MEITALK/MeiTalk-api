package com.meitalk.api.model.stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class StreamResponse {

    private Long vodId;
    private Long channelId;
    private String name;
    private String profile;
    private Long viewCount;
    private Boolean isLive;
    private Boolean isSave;
    private String streamTime;
    private String streamLen;
    private Integer likeCount;
    private String title;
    private String detail;
    private String playbackUrl;
    private String channelArn;
    private String thumbnail;
    private String playToken;
    private String chatKey;
    private Boolean isOwner;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String displayFlag;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer commentCount;

    @JsonIgnore
    private String prefix;

    @Data
    public static class Detail {
        private Long channelId;
        private Long vodId;
        private String title;
        private String detail;
        private int category;
        private Long owner;
        private String name;
        private String profile;
        private Boolean isLike;
        private Boolean isSave;
        private Boolean isPublic;
        private Integer likeCount;
        private String streamTime;
        private String playbackUrl;
        private String chatKey;
        private Integer viewCount;
        private String playToken;
        private StreamDto.VodUrls vodUrls;
        private String thumbnail;
        private StreamDto.SubtitleUrls captions;

        @JsonIgnore
        private String subtitleKey;
        @JsonIgnore
        private String vodPrefix;
        @JsonIgnore
        private String streamLength;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class List {
        java.util.List<StreamResponse> streams;

        private Integer pageNo;
        private Integer pageSize;
        private Integer totalElements;
    }

    @Data
    public static class Lookup {
        private Long vodId;
        private Long channelId;
        private Long userNo;
        private String streamId;
        private String vodTitle;
        private String vodDetail;
        private int vodCategory;

        private String playbackUrl;
        private String chatKey;
        private String transmittedType;
        private String startTime;
    }

    @Data
    @Builder
    public static class AfterWatchVideo {

        private boolean saveToggle;
        private Long vodId;
    }

    @Data
    @Builder
    public static class Ready {
        private String token;
    }
}
