package com.meitalk.api.mapper;

import com.meitalk.api.model.stream.ChannelDto;
import com.meitalk.api.model.stream.ChannelRequest;
import com.meitalk.api.model.stream.ChannelResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Mapper
public interface ChannelMapper {

    boolean isExistChannel(Long userNo);

    Integer saveChannel(ChannelDto.Save saveChannelDto);

    ChannelResponse.Create findUserChannelValue(Long userNo);

    void deleteChannel(Long channelId);

    void reissueStreamKey(ChannelDto.ReissueStreamKey request);

    Optional<ChannelResponse> findChannelByChannelId(Long channelId);

    void deleteStreamKey(Long channelId);

    Optional<ChannelResponse.Arn> findUserChannelArn(Long userNo);

    Optional<ChannelResponse.FindUser> findUserChannel(Long userNo);

    Optional<ChannelResponse> findChannelByChannelArn(String channelArn);

    int updateChannelBannerImage(
            @Param("channelId") Long channelId,
            @Param("imageUrl") String imageUrl
    );

    ChannelResponse.ChannelDetail findChannelDetailByChannelId(Long channelId);

    int updateChannelDetailByChannelId(
            @Param("channelId") Long channelId,
            @Param("req") ChannelRequest.ChannelUpdate req
    );

    int insertMyChannel(Long userNo);
}
