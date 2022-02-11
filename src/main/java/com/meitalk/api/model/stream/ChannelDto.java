package com.meitalk.api.model.stream;

import lombok.Builder;
import lombok.Data;

public class ChannelDto {

    @Data
    @Builder
    public static class Save {
        private Long userNo;
        private String channelArn;
        private String playbackUrl;
        private String streamKey;
        private String streamArn;
        private String chatKey;
        private String playToken;
    }

    @Data
    @Builder
    public static class ReissueStreamKey {
        private Long channelId;
        private String streamKey;
        private String streamArn;
    }
}
