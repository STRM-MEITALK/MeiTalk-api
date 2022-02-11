package com.meitalk.api.model.stream;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class ChannelRequest {

    @Data
    public static class Create {
        private Long userNo;
        private String channelName;
    }

    @Data
    public static class Update {
        private String channelArn;
        private Boolean authorized;
        private String latencyMode;
        private String name;
        private String recordingConfigurationArn;
        private String type;
    }

    @Getter
    @Setter
    @ToString
    public static class ChannelUpdate {
        private String description;
    }

    @Getter
    @Setter
    @ToString
    public static class CreateInternalChannel {
        private Long userNo;
    }
}
