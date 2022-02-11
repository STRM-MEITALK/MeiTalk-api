package com.meitalk.api.model.stream;

import com.meitalk.api.model.stream.enums.CategoryType;
import com.meitalk.api.model.stream.enums.StreamType;
import com.meitalk.api.model.stream.enums.VideoOrderType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

public class StreamRequest {

    @Data
    public static class Start {
        private Long userNo;
        private String vodTitle;
        private String vodDetail;
        private CategoryType vodCategory;
    }

    @Data
    public static class End {
        private Long userNo;
        private Long vodId;
        private Boolean isPublic = false;
    }

    @Data
    public static class After {
        private Boolean isSave;
    }

    @Data
    public static class List {
        private StreamType type;
        private Integer pageNo;
        private Integer pageSize;
        private Long vodId;

        @JsonIgnore
        private Integer offset;
        @JsonIgnore
        private String flag;
        @JsonIgnore
        private Long userNo;
    }

    @Data
    public static class Update {
        private Long vodId;
        private String vodTitle;
        private String vodDetail;
        private CategoryType vodCategory;
        private boolean isPublic;

        private Long userNo;
    }

    @Data
    public static class MyList {
        private VideoOrderType orderType;
        private Integer pageNo;
        private Integer pageSize;
        private Integer offset;
        private String search;

        private Boolean isPublic;

        private Long userNo;
    }

    @Data
    public static class WatchedVideoList {

        private VideoOrderType orderType;
        private Integer pageNo;
        private Integer pageSize;
        private Integer offset;

        private String search;

        private Long userNo;
        private String type;
    }

    @Data
    public static class Ready {
        private String token;
    }
}
