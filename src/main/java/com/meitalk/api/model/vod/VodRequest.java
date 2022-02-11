package com.meitalk.api.model.vod;

import com.meitalk.api.model.stream.enums.CategoryType;
import lombok.Data;

public class VodRequest {

    @Data
    public static class UpdateDetail {

        private String title;
        private String description;
        private CategoryType category;
        private Long channelId;
    }
}
