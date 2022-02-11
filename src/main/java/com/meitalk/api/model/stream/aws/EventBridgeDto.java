package com.meitalk.api.model.stream.aws;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

public class EventBridgeDto {

    @Data
    public static class Request {
        private String version;
        private String id;
        @JsonProperty(value = "detail-type")
        private String detail_type;
        private String source;
        private String account;
        private Date time;
        private String region;
        private List<String> resources;
        private Detail detail;

        private Long vodId;
        private String vodUrl;
        private String thumbnail;

        public String getEventType() {
            return detail.getEvent_name() != null ? detail.getEvent_name()
                    : detail.getLimit_name() != null ? detail.getLimit_name()
                    : detail.getRecording_status() != null ? detail.getRecording_status()
                    : "Unexpected";
        }
    }

    @Data
    public static class Detail {

        private String event_name;
        private String channel_name;
        private String stream_id;
        private String reason;

        private String limit_name;
        private Integer limit_value;
        private Integer exceeded_by;
        private String limit_unit;

        private String recording_status;
        private String recording_status_reason;
        private String recording_s3_bucket_name;
        private String recording_s3_key_prefix;
        private Long recording_duration_ms;
    }

    @Data
    public static class StartStream {

        private Long vodId;
        private Long userNo;
        private String chatKey;
        private String transmittedType;
        private String startTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StartRecording {

        private Long channelId;
        private Long userNo;
        private String streamId;
        private String playbackUrl;
        private String vodTitle;
        private String vodDetail;
        private String vodCategory;
        private Date startTime;
        private String prefix;
        private String transmittedType;
    }
}
