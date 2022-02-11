package com.meitalk.api.controller;

import com.meitalk.api.common.annotation.JwtAuthentication;
import com.meitalk.api.common.annotation.RedisCacheEvict;
import com.meitalk.api.common.enums.Role;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.stream.StreamRequest;
import com.meitalk.api.model.stream.aws.EventBridgeDto;
import com.meitalk.api.model.stream.enums.EventBridgeStatus;
import com.meitalk.api.model.stream.enums.StreamType;
import com.meitalk.api.model.stream.enums.WatchedMenuType;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.service.StreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@Slf4j
@RequestMapping("/api/streams")
@RequiredArgsConstructor
public class StreamController {

    private final StreamService streamService;

    @GetMapping("/check")
    public ResponseWithData isLive(
            @RequestBody StreamRequest.End dto
    ) {
        return ResponseWithData.success(streamService.isLive(dto.getUserNo()));
    }

    @GetMapping
    public ResponseWithData getStreamList(@JwtAuthentication(required = false) JwtUser user,
                                          StreamRequest.List request) {
        request.setUserNo(user != null ? user.getUserNo() : null);
        return ResponseWithData.success(streamService.getStreamList(request));
    }

    @GetMapping("/detail/{vodId}/{streamType}")
    public ResponseWithData getDetailStream(
            @JwtAuthentication(required = false) JwtUser user,
            @PathVariable Long vodId,
            @PathVariable StreamType streamType
    ) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        return ResponseWithData.success(streamService.getStream(user, vodId, streamType));
    }

    @GetMapping("/info/{userNo}")
    public ResponseWithData lookupStream(
            @PathVariable Long userNo
    ) {
        return ResponseWithData.success(streamService.lookupStream(userNo));
    }

    @GetMapping("/watched/{type}")
    public ResponseWithData getWatchedVideoList(@JwtAuthentication JwtUser user,
                                                @PathVariable WatchedMenuType type,
                                                StreamRequest.WatchedVideoList request) {
        request.setUserNo(user.getUserNo());
        request.setType(type.getValue());
        return ResponseWithData.success(streamService.getWatchedVideoList(request));
    }

    @GetMapping("/ready")
    public ResponseWithData getStreamReadyToken(@JwtAuthentication(role = Role.STREAMER) JwtUser user) {
        return ResponseWithData.success(streamService.getStreamReadyToken(user));
    }

    @PostMapping("/ready")
    public ResponseWithData verifyStreamReadyState(@JwtAuthentication(role = Role.STREAMER) JwtUser user,
                                                   @RequestBody StreamRequest.Ready readyState) {
        return ResponseWithData.success(streamService.verifyStreamReadyToken(user, readyState));
    }

    @PostMapping("/{vodId}/after")
    public ResponseWithData toggleAfterWatchVideo(@JwtAuthentication JwtUser user,
                                                  @PathVariable Long vodId,
                                                  @RequestBody(required = false) StreamRequest.After request) {
        return ResponseWithData.success(streamService.toggleAfterWatchVideo(user.getUserNo(), vodId, request));
    }

    @PostMapping()
    public ResponseWithData startStreaming(
        @JwtAuthentication(role = Role.STREAMER) JwtUser user,
        @RequestBody StreamRequest.Start dto
    ) {
        streamService.startStreaming(user, dto);
        return ResponseWithData.success().message("streaming 정보 저장완료");
    }

    @PostMapping("/end")
    public ResponseWithData endStreaming(
        @JwtAuthentication(role = Role.STREAMER) JwtUser user,
        @RequestBody StreamRequest.End request
    ) {
        streamService.endStreaming(request, user.getUserNo());
        return ResponseWithData.success().message("Stream 종료");
    }

    @PostMapping("/event")
    public void streamEvent(
        @RequestBody EventBridgeDto.Request dto
    ) {
        String status = dto.getEventType();
        log.info("Event Bridge ==> {}, {} : {}", dto.getDetail_type(), status, dto);

        if (status.equals(EventBridgeStatus.RECORDING_END.getValue())) {
            streamService.endRecordingEvent(dto);
        } else if (status.equals(EventBridgeStatus.RECORDING_START.getValue())) {
            streamService.startRecordingEvent(dto);
        } else if (status.equals(EventBridgeStatus.STREAM_START.getValue())) {
            streamService.startStreamEvent(dto);
        }


    }

    @PostMapping("/event/all")
    public void streamAllEvent(@RequestBody EventBridgeDto.Request dto) {
        log.info("{} ==> {}", dto.getDetail_type(), dto.getEventType());
    }

    @RedisCacheEvict
    @PatchMapping("/{vodId}")
    public ResponseWithData updateStreaming(
            @JwtAuthentication JwtUser user,
            @RequestBody StreamRequest.Update dto,
            @PathVariable Long vodId
    ) {
        streamService.updateStreaming(user, dto, vodId);
        return ResponseWithData.success();
    }

    @PatchMapping("/{vodId}/count")
    public ResponseWithData addViewCount(@PathVariable Long vodId) {
        return ResponseWithData.success(streamService.addViewCount(vodId));
    }

    @RedisCacheEvict
    @PatchMapping("/{vodId}/display")
    public ResponseWithData displayToggle(@JwtAuthentication(role = Role.STREAMER) JwtUser user, @PathVariable Long vodId) {
        return ResponseWithData.success(streamService.displayToggle(user.getUserNo(), vodId));
    }

    @DeleteMapping("/flag")
    public ResponseWithData deleteFlagX(@JwtAuthentication(role = Role.STREAMER) JwtUser user) {
        return ResponseWithData.success(streamService.deleteInternalFlagX(user.getUserNo()));
    }

    @RedisCacheEvict
    @DeleteMapping("/{vodId}")
    public ResponseWithData deleteVod(@JwtAuthentication(role = Role.STREAMER) JwtUser user, @PathVariable Long vodId) {
        return ResponseWithData.success(streamService.deleteVod(user.getUserNo(), vodId));
    }

}
