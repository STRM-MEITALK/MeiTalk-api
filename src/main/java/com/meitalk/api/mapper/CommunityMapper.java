package com.meitalk.api.mapper;

import com.meitalk.api.model.community.CommunityDto;
import com.meitalk.api.model.community.CommunityRequest;
import com.meitalk.api.model.community.CommunityResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CommunityMapper {

    void createCommunityBoard(CommunityDto.CreateCommunityBoard request);

    List<CommunityDto.CommunityList> findCommunityByChannelId(
            Long channelId,
            @Param("index") Integer index,
            @Param("pageSize") Integer pageSize
    );

    CommunityResponse.CommunityDetail findCommunityDetailByCommunityId(Long communityId);

    int updateCommunityByCommunityId(
            @Param("communityId") Long communityId,
            @Param("req") CommunityRequest.CommunityUpdate req
    );

    int deleteCommunityByCommunityId(Long communityId);

    Integer getTotalCommunity(Long channelId);
}
