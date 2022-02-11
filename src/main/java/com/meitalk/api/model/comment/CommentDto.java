package com.meitalk.api.model.comment;

import com.meitalk.api.model.TypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class CommentDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class CommonParam {
        private Long id;
        private Long userNo;
        private Long contentId;
        private String content;
        private String type;
        private String updateTime;

        public CommonParam(Long userNo, CommentRequest commentRequest) {
            this.id = 0L;
            this.userNo=userNo;
            this.contentId = commentRequest.getContentId();
            this.content = commentRequest.getContent();
            this.type = commentRequest.getType();
        }
        public CommonParam(Long userNo, CommentRequest commentRequest, TypeEnum type) {
            this.id = 0L;
            this.userNo=userNo;
            this.contentId = commentRequest.getContentId();
            this.content = commentRequest.getContent();
            this.type = type.getDbCode();
        }
    }
}
