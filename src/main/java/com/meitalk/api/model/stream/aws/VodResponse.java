package com.meitalk.api.model.stream.aws;

import lombok.Data;

import java.util.List;

@Data
public class VodResponse {

    private Media media;

    @Data
    public static class Media {

        private Hls hls;

        @Data
        public static class Hls{

            private String path;
            private String playlist;
            private List<Rendition> renditions;

            @Data
            public static class Rendition {

                private String path;
                private String playlist;
            }
        }
    }
}
