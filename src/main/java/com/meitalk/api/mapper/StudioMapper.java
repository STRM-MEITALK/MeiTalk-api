package com.meitalk.api.mapper;

import com.meitalk.api.model.stream.StreamRequest;
import com.meitalk.api.model.stream.StreamResponse;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Mapper
public interface StudioMapper {

    List<StreamResponse> getMyStreamList(StreamRequest.MyList request);

    Integer getMyTotalStreams(Long userNo, Boolean isPublic, String search);

    Optional<StreamResponse.Detail> getUserVodDetail(Long userNo, Long vodId);

    Long getCommentIdByReplyId(Long id);

    int deleteComment(Long userNo, Long id, String type);

    Optional<Long> getVodOwner(Long commentId);
}
