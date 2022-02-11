package com.meitalk.api.service;

import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.exception.StreamException;
import com.meitalk.api.mapper.ChannelMapper;
import com.meitalk.api.mapper.CommunityMapper;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.community.CommunityDto;
import com.meitalk.api.model.community.CommunityRequest;
import com.meitalk.api.model.community.CommunityResponse;
import com.meitalk.api.model.stream.ChannelResponse;
import com.meitalk.api.model.user.JwtUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.meitalk.api.exception.ResponseCodeEnum.ACCESS_DENIED;
import static com.meitalk.api.exception.ResponseCodeEnum.RESULT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityService {

    private final ChannelMapper channelMapper;
    private final CommunityMapper communityMapper;


    @Transactional
    public ResponseWithData createCommunityBoard(JwtUser user, CommunityDto.Request request) {
        verifyChannelOwner(user.getUserNo(), request.getChannelId());
        CommunityDto.CreateCommunityBoard req = CommunityDto.CreateCommunityBoard.builder()
                .channelId(request.getChannelId())
                .contents(request.getContents())
                .build();

        communityMapper.createCommunityBoard(req);
        return ResponseWithData.success(communityMapper.findCommunityDetailByCommunityId(req.getId()));
    }

    public ResponseWithData getCommunityBoardList(JwtUser user, Integer pageNo, Integer pageSize, Long channelId) {
        ChannelResponse channel = channelMapper.findChannelByChannelId(channelId)
                .orElseThrow(() -> new StreamException(RESULT_NOT_FOUND));

        List<CommunityDto.CommunityList> list = communityMapper.findCommunityByChannelId(channelId, pageNo * pageSize, pageSize);

        Integer totalElements = communityMapper.getTotalCommunity(channelId);
        CommunityDto.CommunityListWrapper result = CommunityDto.CommunityListWrapper.builder()
                .communityList(list)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .build();

        return ResponseWithData.success(result);
    }


    private void verifyChannelOwner(Long userNo, Long channelId) {
        ChannelResponse channel = channelMapper.findChannelByChannelId(channelId)
                .orElseThrow(() -> new StreamException(RESULT_NOT_FOUND));
        if (!channel.getUserNo().equals(userNo))
            throw new StreamException(ACCESS_DENIED);
    }

    public ResponseWithData getCommunityDetail(JwtUser user, Long communityId) {
        CommunityResponse.CommunityDetail communityDetail = communityMapper.findCommunityDetailByCommunityId(communityId);
        if (communityDetail == null) {
            return ResponseWithData.failed(ResponseCodeEnum.RESULT_NOT_FOUND);
        }
        return ResponseWithData.success(communityDetail);
    }

    @Transactional
    public ResponseWithData updateCommunityBoard(JwtUser user, Long channelId, Long communityId, CommunityRequest.CommunityUpdate req) {
        verifyChannelOwner(user.getUserNo(), channelId);
        CommunityResponse.CommunityDetail communityDetail = communityMapper.findCommunityDetailByCommunityId(communityId);
        if (communityDetail == null) {
            return ResponseWithData.failed(ResponseCodeEnum.RESULT_NOT_FOUND);
        }

        int updateSuccess = communityMapper.updateCommunityByCommunityId(communityId, req);
        if (updateSuccess == 0) {
            return ResponseWithData.failed(ResponseCodeEnum.DATABASE_ERROR);
        }

        CommunityResponse.CommunityDetail result = communityMapper.findCommunityDetailByCommunityId(communityId);
        return ResponseWithData.success(result);
    }

    @Transactional
    public ResponseWithData deleteCommunityBoard(JwtUser user, Long channelId, Long communityId) {
        verifyChannelOwner(user.getUserNo(), channelId);
        CommunityResponse.CommunityDetail communityDetail = communityMapper.findCommunityDetailByCommunityId(communityId);
        if (communityDetail == null) {
            return ResponseWithData.failed(ResponseCodeEnum.RESULT_NOT_FOUND);
        }

        int deleteSuccess = communityMapper.deleteCommunityByCommunityId(communityId);
        if (deleteSuccess == 0) {
            return ResponseWithData.failed(ResponseCodeEnum.DATABASE_ERROR);
        }

        return ResponseWithData.success(communityId);
    }
}
