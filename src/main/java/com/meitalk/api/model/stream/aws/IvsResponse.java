package com.meitalk.api.model.stream.aws;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class IvsResponse {

    @Data
    @Builder
    public static class CreateChannel {

        private String streamArn;
        private String streamKey;
        private String playbackUrl;
        private String channelName;
        private String channelArn;
    }

    @Data
    @Builder
    public static class UpdateChannel {

        private String channelArn;
        private Boolean authorized;
        private String ingestEndpoint;
        private String latencyMode;
        private String name;
        private String recordingConfigurationArn;
        private Map<String, String> tags;
        private String type;
    }

    @Data
    @Builder
    public static class DeleteChannel {

        private boolean isDeleted;
    }

    @Data
    @Builder
    public static class CreateStreamKey {

        private String streamKey;
        private String streamArn;
    }

    @Data
    public static class DeleteStreamKey {

        private boolean isDeleted;
    }

    @Data
    @Builder
    public static class GetStream {

        private String channelArn;
        private String health;
        private String playbackUrl;
        private Date startTime;
        private String state;
        private String streamId;
        private Long viewerCount;
    }

    @Data
    @Builder
    public static class StopStream {

        private boolean isStopped;
    }

    @Data
    public static class ListChannel {

        private List<ChannelSummary> channels;
    }

    @Data
    public static class ChannelSummary {

        private String channelArn;
        private Boolean authorized;
        private String latencyMode;
        private String name;
        private String recordingConfigurationArn;
        private Map<String, String> tags;
    }

    @Data
    public static class ListStreamKey {

        private List<StreamKeySummary> streamKeys;
    }

    @Data
    public static class StreamKeySummary {

        private String StreamKeyArn;
        private String channelArn;
        private Map<String, String> tags;
    }

    @Data
    public static class GetStreamKey {

        private String StreamKeyArn;
        private String channelArn;
        private Map<String, String> tags;
        private String value;
    }
}
