package com.meitalk.api.service;

import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.exception.StreamException;
import com.meitalk.api.mapper.LikeMapper;
import com.meitalk.api.model.LikeRequest;
import com.meitalk.api.model.TypeEnum;
import com.meitalk.api.model.stream.LikeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeMapper likeMapper;

    @Transactional
    public LikeResponse toggleLike(Long userNo, Long typeId, String typeParam, LikeRequest request) {
        TypeEnum type = TypeEnum.findByParam(typeParam);
        Boolean isLike = request != null ? request.getIsLike() : null;
        boolean likeToggle = false;
        if(type==null){
            throw new StreamException(ResponseCodeEnum.INVALID_PARAMETER);
        }
        int count = likeMapper.countLikeExists(userNo,typeId, type.getDbCode());
        int queryCount = 0;
        long sumLikeCount = 0;

        if (count > 0) {//update
            queryCount = likeMapper.updateLikeToggle(userNo, typeId, type.getDbCode(), isLike);
        } else {//insert
            if (isLike == null || isLike) {
                queryCount = likeMapper.insertLike(userNo, typeId, type.getDbCode());
            }
        }
        sumLikeCount = likeMapper.selectLikeCountByTypeIdAndType(typeId, type.getDbCode());

        likeToggle = 1 == likeMapper.selectLikeByTypeIdAndType(userNo,typeId, type.getDbCode());

        if(queryCount!=1){
             throw new StreamException(ResponseCodeEnum.UNKNOWN_ERROR);
        }
        Long commentId;
        if (type.equals(TypeEnum.COMMENT_REPLY)){
            commentId = likeMapper.selectCommentIdByReplyId(typeId);
            return LikeResponse.builder()
                    .commentId(commentId)
                    .userNo(userNo)
                    .likeCount(sumLikeCount)
                    .typeId(typeId)
                    .type(typeParam)
                    .likeToggle(likeToggle)
                    .build();
        }

        return LikeResponse.builder()
                .userNo(userNo)
                .likeCount(sumLikeCount)
                .typeId(typeId)
                .type(typeParam)
                .likeToggle(likeToggle)
                .build();
    }
}
