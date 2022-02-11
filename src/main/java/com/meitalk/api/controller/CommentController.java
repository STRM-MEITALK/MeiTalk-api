package com.meitalk.api.controller;

import com.meitalk.api.common.annotation.JwtAuthentication;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.comment.CommentRequest;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{video-id}")
    public ResponseWithData commentList(@JwtAuthentication(required = false) JwtUser user,
                                        @PathVariable("video-id") Long videoId,
                                        @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return commentService.getCommentListByVodId(videoId, pageNo, pageSize, user);
    }

    @GetMapping("/reply/{comment-id}")
    public ResponseWithData commentReplyList(@JwtAuthentication(required = false) JwtUser jwtUser,
                                             @PathVariable("comment-id") Long commentId) {

        return commentService.getCommentReplyListByCommentId(commentId, jwtUser);
    }

    @GetMapping("/community/{community-id}")
    public ResponseWithData communityCommentList(@JwtAuthentication(required = false) JwtUser jwtUser,
                                                 @PathVariable("community-id") Long communityId,
                                                 @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        return commentService.getCommunityCommentListByCommunityId(communityId, pageNo, pageSize, jwtUser);
    }

    @GetMapping("/community/reply/{community-comment-id}")
    public ResponseWithData communityCommentReplyListByCommentId(@JwtAuthentication(required = false) JwtUser jwtUser,
                                                                 @PathVariable("community-comment-id") Long communityCommentId) {

        return commentService.getCommunityCommentReplyListByCommentId(communityCommentId, jwtUser);
    }

    @PostMapping("")
    public ResponseWithData createComment(@JwtAuthentication JwtUser user,
                                          @RequestBody CommentRequest request) {

        return commentService.createComment(user.getUserNo(), request);
    }

    @PutMapping("/{id}")
    public ResponseWithData editComment(@JwtAuthentication JwtUser user,
                                        @RequestBody CommentRequest request,
                                        @PathVariable("id") Long id) {

        return commentService.editComment(user.getUserNo(), request, id);
    }

    @DeleteMapping("/{id}")
    public ResponseWithData deleteComment(@JwtAuthentication JwtUser user,
                                          CommentRequest request,
                                          @PathVariable("id") Long id) {

        return commentService.deleteComment(user.getUserNo(), request, id);
    }
}
