package com.meitalk.api.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface LikeMapper {
    int countLikeExists(
            @Param("userNo") Long userNo,
            @Param("typeId") Long typeId,
            @Param("type") String type
    );

    int insertLike(
            @Param("userNo") Long userNo,
            @Param("typeId") Long typeId,
            @Param("type") String type);

    int updateLikeToggle(
            @Param("userNo") Long userNo,
            @Param("typeId") Long typeId,
            @Param("type") String type,
            @Param("isLike") Boolean isLike);

    long selectLikeCountByTypeIdAndType(
            @Param("typeId")Long typeId,
            @Param("type") String type
    );

    int selectLikeByTypeIdAndType(
            @Param("userNo") Long userNo,
            @Param("typeId") Long typeId,
            @Param("type") String type
    );

    Long selectCommentIdByReplyId(Long typeId);
}
