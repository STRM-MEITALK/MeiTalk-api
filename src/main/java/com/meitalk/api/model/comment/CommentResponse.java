package com.meitalk.api.model.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CommentResponse {

    private Long contentId;
    private Long channelId;
    private Long id;
    private Long userNo;
    private String userName;
    private Long vodId;
    private Long communityId;
    private String commentContent;
    private int likeCount;
    private String userPicture;
    private boolean likeIs;
    private int replyCount;
    private String createTime;
    private String updateTime;
    private boolean deleted;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Delete {

        private Long commentId;
        private Long replyId;
        private String type;
    }

    @Getter
    @NoArgsConstructor
    public static class Page {
        private List<CommentResponse> list;
        private Integer pageNo;
        private Integer pageSize;
        private Integer totalElements;
        private int totalComments;

        @Builder
        public Page(List<CommentResponse> list, Integer pageNo, Integer pageSize, Integer totalElements, int totalComments) {
            this.list = list;
            this.pageNo = pageNo;
            this.pageSize = pageSize;
            this.totalElements = totalElements;
            this.totalComments = totalComments;
        }
    }

    @Getter
    @Setter
    @ToString
    public static class ReplyList {

        private Long commentId;
        private List<Reply> list;

        @Builder
        public ReplyList(Long commentId, List<Reply> list) {
            this.commentId = commentId;
            this.list = list;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class Reply {
        private Long channelId;
        private Long id;
        private Long userNo;
        private String userName;
        private String replyContent;
        private int likeCount;
        private String userPicture;
        private String createTime;
        private String updateTime;
        private boolean likeIs;
        private boolean deleted;
    }

}
