package com.meitalk.api.model.vod;

import lombok.Builder;
import lombok.Data;

public class VodDto {

    @Data
    @Builder
    public static class GetThumbnailUrl {

        private String vodPrefix;
        private String thumbnailPath;
    }

    @Builder
    @Data
    public static class UploadImage {

        private String url;
        private String name;
    }

}
