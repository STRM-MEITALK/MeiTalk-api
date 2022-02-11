package com.meitalk.api.mapper;

import com.meitalk.api.model.vod.VodDto;
import com.meitalk.api.model.vod.VodRequest;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Mapper
public interface VodMapper {

    Optional<Long> getChannelIdByChatKey(String chatKey);

    void updateVod(VodRequest.UpdateDetail dto);

    Optional<VodDto.GetThumbnailUrl> getThumbnailUrl(Long vodId, Long userNo);

    void saveThumbnailPath(String thumbnailUrl, Long vodId);
}
