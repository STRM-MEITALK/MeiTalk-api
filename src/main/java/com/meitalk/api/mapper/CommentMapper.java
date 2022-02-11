package com.meitalk.api.mapper;

import com.meitalk.api.model.comment.CommentDto;
import com.meitalk.api.model.comment.CommentResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentMapper {
    List<CommentResponse> getCommentsByVideoId(@Param("id") Long id, @Param("index")Integer index, @Param("pageSize")Integer pageSize, @Param("userNo")Long userNo);

    int insertComment(CommentDto.CommonParam request);

//    int insertCommentReply(CommentDto.CommonParam request);
//
//    int insertCommunityComment(CommentDto.CommonParam request);
//
//    int insertCommunityCommentReply(CommentDto.CommonParam request);

    int countCommentByUserNoAndId(@Param("userNo") Long userNo, @Param("id") Long id, @Param("type") String type);

    int updateComment(@Param("userNo") Long userNo, @Param("id") Long id, @Param("content") String content, @Param("type") String type);

    int deleteComment(@Param("userNo") Long userNo, @Param("id") Long id, @Param("type") String type);

    List<CommentResponse.Reply> getCommentReplyListByCommentId(Long id, Long userNo);

    int getCountCommentByVideoId(Long id);

    CommentResponse selectCommentByCommentId(@Param("id") Long id, @Param("type")String type);

//    CommentResponse selectCommentReplyByCommentReplyId(Long id);
//
//    CommentResponse selectCommunityCommentByCommentId(Long id);
//
//    CommentResponse selectCommunityCommentReplyByCommentReplyId(Long id);

    Long getCommentIdByReplyId(Long replyId);

    int getTotalComments(Long id);

    String getUpdateTime(Long userNo, Long id, String type);

    Long getCommunityCommentIdByReplyId(Long replyId);

    List<CommentResponse> getCommunityCommentListByCommunityId(@Param("id") Long id, @Param("index")Integer index, @Param("pageSize")Integer pageSize, @Param("userNo")Long userNo);

    List<CommentResponse.Reply> getCommunityCommentReplyListByCommentId(Long id, Long userNo);

    int getCountCommunityCommentByVideoId(Long id);

    int getTotalCommunityComments(Long id);
}
