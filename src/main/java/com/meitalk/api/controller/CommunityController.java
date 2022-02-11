package com.meitalk.api.controller;

import com.meitalk.api.common.annotation.JwtAuthentication;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.community.CommunityDto;
import com.meitalk.api.model.community.CommunityRequest;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping("/{channelId}")
    public ResponseWithData getCommunityList(@PathVariable("channelId") Long channelId,
                                             @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
                                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                             @JwtAuthentication(required = false) JwtUser user) {
        return communityService.getCommunityBoardList(user, pageNo, pageSize, channelId);
    }

    @GetMapping("/detail/{communityId}")
    public ResponseWithData getComminityDetail(@JwtAuthentication(required = false) JwtUser user,
                                               @PathVariable("communityId") Long communityId) {
        return communityService.getCommunityDetail(user, communityId);
    }


    @PostMapping("")
    public ResponseWithData createCommunityBoard(@JwtAuthentication JwtUser user,
                                                 @RequestBody CommunityDto.Request request) {
        return communityService.createCommunityBoard(user, request);
    }

    @PatchMapping("/{channelId}/{communityId}")
    public ResponseWithData updateCommunityBoard(@JwtAuthentication JwtUser user,
                                                 @PathVariable("channelId") Long channelId,
                                                 @PathVariable("communityId") Long communityId,
                                                 @RequestBody CommunityRequest.CommunityUpdate req) {
        return communityService.updateCommunityBoard(user, channelId, communityId, req);
    }

    @DeleteMapping("/{channelId}/{communityId}")
    public ResponseWithData deleteCommunityBoard(@JwtAuthentication JwtUser user,
                                                 @PathVariable("channelId") Long channelId,
                                                 @PathVariable("communityId") Long communityId) {
        return communityService.deleteCommunityBoard(user, channelId, communityId);
    }


}
