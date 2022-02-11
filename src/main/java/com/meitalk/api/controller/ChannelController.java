package com.meitalk.api.controller;

import com.meitalk.api.common.annotation.JwtAuthentication;
import com.meitalk.api.common.enums.Role;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.stream.ChannelRequest;
import com.meitalk.api.model.stream.StreamRequest;
import com.meitalk.api.model.stream.aws.IvsResponse;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.service.ChannelService;
import com.meitalk.api.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;
    private final StreamService streamService;

    @GetMapping("/{channelId}/streams")
    public ResponseWithData getChannelStreamList(@JwtAuthentication(required = false) JwtUser user,
                                                 @PathVariable Long channelId,
                                                 StreamRequest.MyList request) {
        request.setUserNo(user != null ? user.getUserNo() : null);
        return ResponseWithData.success(streamService.getChannelStreamList(channelId, request));
    }

    @PostMapping
    public ResponseWithData createIvsChannel(
            @JwtAuthentication(role = Role.STREAMER) JwtUser user,
            @RequestBody ChannelRequest.Create dto
    ) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        return ResponseWithData.success(channelService.createIvsChannel(user, dto));
    }

    @PostMapping("/{channelId}/key")
    public ResponseWithData issueStreamKey(@JwtAuthentication(role = Role.STREAMER) JwtUser user,
                                           @PathVariable Long channelId) {
        channelService.issueStreamKey(user, channelId);
        return ResponseWithData.success();
    }

    @PatchMapping("/{channelId}/key")
    public ResponseWithData reissueStreamKey(
            @JwtAuthentication(role = Role.STREAMER) JwtUser user,
            @PathVariable Long channelId
    ) {
        channelService.reissueStreamKey(user, channelId);
        return ResponseWithData.success();
    }

    @PutMapping("/{channelId}")
    public ResponseWithData updateChannel(@JwtAuthentication(role = Role.STREAMER) JwtUser user,
                                          @PathVariable Long channelId,
                                          @RequestBody ChannelRequest.Update reqUpdateChannel) {

        IvsResponse.UpdateChannel result = channelService.updateChannel(user.getUserNo(), channelId, reqUpdateChannel);

        return ResponseWithData.success(result);
    }

    @DeleteMapping("/{channelId}")
    public ResponseWithData deleteChannel(
            @JwtAuthentication(role = Role.STREAMER) JwtUser user,
            @PathVariable Long channelId
    ) {
        channelService.deleteChannel(user, channelId);
        return ResponseWithData.success();
    }

    @DeleteMapping("/{channelId}/key")
    public ResponseWithData deleteStreamKey(
            @JwtAuthentication(role = Role.STREAMER) JwtUser user,
            @PathVariable Long channelId
    ) {
        channelService.deleteStreamKey(user, channelId);
        return ResponseWithData.success();
    }

    @PostMapping("/create/internal")
    public ResponseWithData createInternalChannel(ChannelRequest.CreateInternalChannel req) {
        return channelService.createInternalChannel(req);
    }

    @PostMapping(value = "/{channelId}/banner")
    public ResponseWithData channelBannerUpload(
            @JwtAuthentication(role = Role.STREAMER) JwtUser user,
            @PathVariable Long channelId,
            @RequestParam("file") MultipartFile profileImage
    ) throws IOException {
        return channelService.channelBannerUpload(user.getUserNo(), channelId, profileImage);
    }

    @GetMapping("/{channelId}")
    public ResponseWithData getChannel(@JwtAuthentication(required = false) JwtUser user,
                                       @PathVariable Long channelId) {
        return channelService.getChannel(user, channelId);
    }

    @PatchMapping("/{channelId}")
    public ResponseWithData updateChannelDetail(@JwtAuthentication JwtUser user,
                                                @PathVariable Long channelId,
                                                @RequestBody ChannelRequest.ChannelUpdate req) {
        return channelService.updateChannelDetail(user, channelId, req);
    }

    @DeleteMapping(value = "/{channelId}/banner")
    public ResponseWithData channelBannerDelete(
            @JwtAuthentication(role = Role.STREAMER) JwtUser user,
            @PathVariable Long channelId
    ) throws IOException {
        return channelService.channelBannerDelete(user.getUserNo(), channelId);
    }

}
