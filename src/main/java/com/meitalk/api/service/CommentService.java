package com.meitalk.api.service;

import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.exception.StreamException;
import com.meitalk.api.mapper.CommentMapper;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.TypeEnum;
import com.meitalk.api.model.comment.CommentDto;
import com.meitalk.api.model.comment.CommentRequest;
import com.meitalk.api.model.comment.CommentResponse;
import com.meitalk.api.model.user.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;

    public ResponseWithData getCommentListByVodId(Long id, Integer pageNo, Integer pageSize, JwtUser user) {
        List<CommentResponse> result = commentMapper.getCommentsByVideoId(id, pageNo * pageSize, pageSize, user == null ? null : user.getUserNo());
        int totalElements = commentMapper.getCountCommentByVideoId(id);
        int totalComments = commentMapper.getTotalComments(id);
        result.sort((o1, o2) -> -1);

        return ResponseWithData.success(
                CommentResponse.Page.builder()
                        .list(result)
                        .pageNo(pageNo)
                        .pageSize(pageSize)
                        .totalElements(totalElements)
                        .totalComments(totalComments)
                        .build());
    }

    @Transactional
    public ResponseWithData createComment(Long userNo, CommentRequest request) {
        TypeEnum type = TypeEnum.findByParam(request.getType());
        if (type == null || request.getContentId() == null) {
            throw new StreamException(ResponseCodeEnum.INVALID_PARAMETER);
        }
        CommentDto.CommonParam param = new CommentDto.CommonParam(userNo, request, type);

        int queryCount = commentMapper.insertComment(param);

        if (queryCount != 1) {
            throw new StreamException(ResponseCodeEnum.QUERY_FAILED);
        }
        CommentResponse result = commentMapper.selectCommentByCommentId(param.getId(), type.getDbCode());

        if (type.equals(TypeEnum.COMMENT) | type.equals(TypeEnum.COMMUNITY_COMMENT)) {
            result.setContentId(request.getContentId());
        }

        if (result == null) {
            throw new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND);
        }

        return ResponseWithData.success(result);
    }

    @Transactional
    public ResponseWithData editComment(Long userNo, CommentRequest request, Long id) {
        TypeEnum type = TypeEnum.findByParam(request.getType());

        if (type == null) {
            throw new StreamException(ResponseCodeEnum.INVALID_PARAMETER);
        }

        CommentDto.CommonParam param = new CommentDto.CommonParam(userNo, request);
        param.setId(id);
        if (type.equals(TypeEnum.COMMENT) || type.equals(TypeEnum.COMMUNITY_COMMENT)) {
            param.setContentId(id);
        } else if (type.equals(TypeEnum.COMMENT_REPLY)) {
            param.setContentId(commentMapper.getCommentIdByReplyId(id));
        } else if (type.equals(TypeEnum.COMMUNITY_COMMENT_REPLY)) {
            param.setContentId(commentMapper.getCommunityCommentIdByReplyId(id));
        }

        int commentCount = commentMapper.countCommentByUserNoAndId(userNo, id, type.getDbCode());

        if (commentCount == 0) {
            throw new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND);
        }
        int queryCount = commentMapper.updateComment(userNo, id, request.getContent(), type.getDbCode());

        if (queryCount != 1) {
            throw new StreamException(ResponseCodeEnum.QUERY_FAILED);
        }

        param.setUpdateTime(commentMapper.getUpdateTime(userNo, id, type.getDbCode()));

        return ResponseWithData.success(param);
    }

    @Transactional
    public ResponseWithData deleteComment(Long userNo, CommentRequest request, Long id) {
        TypeEnum type = TypeEnum.findByParam(request.getType());

        Long commentId = null;
        Long replyId = null;

        if (type == null) {
            throw new StreamException(ResponseCodeEnum.INVALID_PARAMETER);
        } else if (type.equals(TypeEnum.COMMENT) || type.equals(TypeEnum.COMMUNITY_COMMENT)) {
            commentId = id;
        } else if (type.equals(TypeEnum.COMMENT_REPLY)) {
            commentId = commentMapper.getCommentIdByReplyId(id);
            replyId = id;
        } else if (type.equals(TypeEnum.COMMUNITY_COMMENT_REPLY)) {
            commentId = commentMapper.getCommunityCommentIdByReplyId(id);
            replyId = id;
        }

        int commentCount = commentMapper.countCommentByUserNoAndId(userNo, id, type.getDbCode());

        if (commentCount == 0) {
            throw new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND);
        }
        int queryCount = commentMapper.deleteComment(userNo, id, type.getDbCode());

        if (queryCount != 1) {
            throw new StreamException(ResponseCodeEnum.QUERY_FAILED);
        }
        return ResponseWithData.success(
                CommentResponse.Delete.builder()
                        .commentId(commentId)
                        .replyId(replyId)
                        .type(request.getType())
                        .build());
    }

    public ResponseWithData getCommentReplyListByCommentId(Long id, JwtUser jwtUser) {
        List<CommentResponse.Reply> resCommentReplyList = commentMapper.getCommentReplyListByCommentId(id, jwtUser == null ? null : jwtUser.getUserNo());
        return ResponseWithData.success(
                CommentResponse.ReplyList.builder()
                        .commentId(id)
                        .list(resCommentReplyList)
                        .build());
    }

    public ResponseWithData getCommunityCommentListByCommunityId(Long id, Integer pageNo, Integer pageSize, JwtUser user) {
        List<CommentResponse> result = commentMapper.getCommunityCommentListByCommunityId(id, pageNo * pageSize, pageSize, user == null ? null : user.getUserNo());
        int totalElements = commentMapper.getCountCommunityCommentByVideoId(id);
        int totalComments = commentMapper.getTotalCommunityComments(id);

        return ResponseWithData.success(
                CommentResponse.Page.builder()
                        .list(result)
                        .pageNo(pageNo)
                        .pageSize(pageSize)
                        .totalElements(totalElements)
                        .totalComments(totalComments)
                        .build());
    }

    public ResponseWithData getCommunityCommentReplyListByCommentId(Long id, JwtUser jwtUser) {
        List<CommentResponse.Reply> resCommentReplyList = commentMapper.getCommunityCommentReplyListByCommentId(id, jwtUser == null ? null : jwtUser.getUserNo());
        return ResponseWithData.success(
                CommentResponse.ReplyList.builder()
                        .commentId(id)
                        .list(resCommentReplyList)
                        .build());
    }
}


