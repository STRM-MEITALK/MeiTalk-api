package com.meitalk.api.model.stream;

import com.meitalk.api.model.stream.enums.CategoryType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

public class StreamDto {

    @Data
    @Builder
    public static class SaveInfo {
        private Long channelId;
        private Long userNo;
        private String playbackUrl;
        private String vodTitle;
        private String vodDetail;
        private CategoryType vodCategory;
        private String transmittedType;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class VodUrls {
        private String auto;
        private String _160p30;
        private String _360p30;
        private String _480p30;
        private String _720p30;
        private String _720p60;
        private String _1080p;
        private String _1080p30;
        private String _1080p60;
        private String _1920p;
    }

    @Data
    @Builder
    public static class SubtitleUrls {

        @JsonProperty("JA")
        private String ja;

        @JsonProperty("EN")
        private String en;

        @JsonProperty("RU")
        private String ru;

        @JsonProperty("KO")
        private String ko;

        @JsonProperty("ZH")
        private String zh;

        @JsonProperty("ID")
        private String id;
    }

    @Data
    @Builder
    public static class ReadyToken {

        private String hash;
        private String salt;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    public static class ListCacheData implements Serializable {
        private String data;
        private int totalElement;

        public ListCacheData(String data, int totalElement) {
            this.data = data;
            this.totalElement = totalElement;
        }
    }
}
