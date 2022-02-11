package com.meitalk.api.model.stream;

import com.meitalk.api.model.user.UserProfile;
import lombok.*;

import java.time.LocalDateTime;

@Data
public class ChannelResponse {

    private Long channelId;
    private Long userNo;
    private String channelName;
    private String channelImage;
    private String channelArn;
    private String playbackUrl;
    private String streamKey;
    private String streamArn;
    private String chatKey;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create {
        private String rtmpUrl;
        private String streamValue;
        private String playbackUrl;
        private String playToken;

    }

    @Data
    public static class FindUser {
        private Long channelId;
        private String playbackUrl;
        private String channelArn;
    }

    @Data
    public static class Arn {
        private String channelArn;
    }

    @Getter
    @Setter
    @ToString
    public static class ChannelDetail {
        private Long channelId;
        private UserProfile userProfile;
        private String chImg;
        private String description;
        private String isMe;
        private Long views;
    }
}
