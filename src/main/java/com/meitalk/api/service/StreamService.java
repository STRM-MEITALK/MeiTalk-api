package com.meitalk.api.service;

import com.meitalk.api.common.annotation.RedisCacheEvict;
import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.exception.StreamException;
import com.meitalk.api.mapper.ChannelMapper;
import com.meitalk.api.mapper.StreamMapper;
import com.meitalk.api.model.stream.ChannelResponse;
import com.meitalk.api.model.stream.StreamDto;
import com.meitalk.api.model.stream.StreamRequest;
import com.meitalk.api.model.stream.StreamResponse;
import com.meitalk.api.model.stream.aws.EventBridgeDto;
import com.meitalk.api.model.stream.aws.IvsResponse;
import com.meitalk.api.model.stream.aws.VodResponse;
import com.meitalk.api.model.stream.enums.RedisKey;
import com.meitalk.api.model.stream.enums.StreamType;
import com.meitalk.api.model.stream.enums.TransmittedType;
import com.meitalk.api.model.stream.enums.VideoOrderType;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.utils.RedisCacheManager;
import com.meitalk.api.utils.SHA256;
import com.meitalk.api.utils.StreamUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamService {

    private final MediaServer mediaServer;
    private final StreamMapper streamMapper;
    private final ChannelMapper channelMapper;
    private final RedisCacheManager redisCacheManager;

    private final RedisTemplate<String, String> redisTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${chat.url}")
    private String chatUrl;

    @Value("${subtitle.url}")
    private String subtitleUrl;

    @Value("${streamReadyKey}")
    private String readyKey;

    @Value("${aws.ivs.cloudFrontUrl}")
    private String cloudFrontUrl;

    @Transactional
    public void startStreaming(JwtUser user, StreamRequest.Start dto) {
        ChannelResponse.FindUser resFindUserChannel = channelMapper.findUserChannel(user.getUserNo())
                .orElseThrow(() -> new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND));
        log.info("findUserChannel result == {}", resFindUserChannel);

        streamMapper.deleteInternalFlagX(user.getUserNo());

        StreamDto.SaveInfo saveStreamInfo = StreamDto.SaveInfo.builder()
                .channelId(resFindUserChannel.getChannelId())
                .userNo(user.getUserNo())
                .playbackUrl(resFindUserChannel.getPlaybackUrl())
                .vodTitle(dto.getVodTitle())
                .vodDetail(dto.getVodDetail())
                .vodCategory(dto.getVodCategory())
                .transmittedType(TransmittedType.INTERNAL.getValue())
                .build();
        streamMapper.saveVodInfo(saveStreamInfo);
    }

    @Transactional
    public void endStreaming(StreamRequest.End request, Long userNo) {
        ChannelResponse.Arn userChannelArn = channelMapper.findUserChannelArn(userNo)
                .orElseThrow(() -> new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND));
        log.info("userChannelArn == {}", userChannelArn);

        IvsResponse.StopStream stopStreamResult = mediaServer.stopStream(userChannelArn.getChannelArn());

        log.info("StopStreamResult == {}", stopStreamResult);

        if (request.getIsPublic()) {
            streamMapper.updateDisplayFlag(request.getVodId());
        }
    }

    @Transactional
    public void updateStreaming(JwtUser user, StreamRequest.Update dto, Long vodId) {
        dto.setVodId(vodId);
        dto.setUserNo(user.getUserNo());
        streamMapper.updateStream(dto);
    }

    public IvsResponse.GetStream isLive(Long userNo) {
        ChannelResponse.Arn userChannelArn = channelMapper.findUserChannelArn(userNo)
                .orElseThrow(() -> new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND));
        log.info("userChannelArn == {}", userChannelArn);

        return mediaServer.getStream(userChannelArn.getChannelArn());
    }

    public StreamResponse.List getStreamList(StreamRequest.List request) {

        String hashKey = redisCacheManager.listCacheRedisHashKey(request.getType().getValue(), request.getPageNo(), request.getPageSize());

        if (request.getPageNo().equals(0) && request.getVodId() == null) {
            StreamResponse.List cacheList = redisCacheManager.getHashType(RedisKey.MAIN_STREAM_LIST, hashKey, StreamResponse.List.class);
            if (cacheList != null) {
                return cacheList;
            }
        }

        request.setFlag(request.getType().getValue());
        request.setOffset(request.getPageNo()*request.getPageSize());

        List<StreamResponse> streamList = streamMapper.getStreamList(request);

        streamList.stream().filter(StreamResponse::getIsLive).forEach(stream -> {
            stream.setThumbnail(StreamUtils.getThumbnailEnd(cloudFrontUrl, stream.getPrefix(), stream.getStreamTime()));
            stream.setViewCount(getUserCount(stream.getChatKey()));
        });

        Integer totalElements = streamMapper.getTotalStreams(request.getFlag(), request.getVodId());

        StreamResponse.List result = StreamResponse.List.builder()
                .streams(streamList)
                .pageNo(request.getPageNo())
                .pageSize(request.getPageSize())
                .totalElements(totalElements)
                .build();

        if (request.getPageNo().equals(0) && request.getVodId() == null) {
            redisCacheManager.setHashType(RedisKey.MAIN_STREAM_LIST, hashKey, result);
        }

        return result;
    }

    public Long getUserCount(String chatKey) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        return Long.valueOf(Optional
                .ofNullable(valueOps.get(RedisKey.USER_COUNT + "_" + chatKey))
                .orElse("0"));
    }

    @RedisCacheEvict
    @Transactional
    public void startStreamEvent(EventBridgeDto.Request dto) {

        String streamId = dto.getDetail().getStream_id();

        streamMapper.startStreamEvent(streamId);

        EventBridgeDto.StartStream result = streamMapper.startStream(streamId);

//        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String startTime = transFormat.format(dto.getTime());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("chatKey", result.getChatKey());
        jsonObject.put("streamerUserNo", result.getUserNo());
        jsonObject.put("transmittedType", result.getTransmittedType());
        jsonObject.put("startTime", result.getStartTime());
        jsonObject.put("vodId", result.getVodId());
        HttpEntity<String> request = new HttpEntity<>(jsonObject.toJSONString(), headers);

        int retry = 1;
        while (retry < 4) {
            try {
                Object response = restTemplate.postForEntity(chatUrl + "/chat/room", request , Object.class);
                if (String.valueOf(response).contains("result=success")) {
                    log.info("create chatting room response -> {}", response);
                    break;
                }
                log.info("chatting server response = {}", response);

            } catch (Exception e) {
                log.info("EventBridge : chatting server disconnect - try " + retry);
            }
            retry ++;
        }
    }

    @RedisCacheEvict
    @Transactional
    public void endRecordingEvent(EventBridgeDto.Request dto) {
        String channelArn = dto.getResources().stream().filter(v -> v.contains("channel")).findFirst()
                .orElseThrow(() -> new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND));

        ChannelResponse channel = channelMapper.findChannelByChannelArn(channelArn)
                .orElseThrow(() -> new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND));

        dto.setVodUrl(StreamUtils.getVodUrl360(cloudFrontUrl, dto.getDetail().getRecording_s3_key_prefix()));
        dto.setThumbnail(StreamUtils.getThumbnailStart(cloudFrontUrl, dto.getDetail().getRecording_s3_key_prefix()));

        int result = streamMapper.endRecordingEvent(dto);
        if (result == 0) {
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("chatKey", channel.getChatKey());

        HttpEntity<String> request = new HttpEntity<>(jsonObject.toJSONString(), headers);

        int retry = 1;

        while (retry < 4) {
            try {
                Object response = restTemplate.postForEntity(chatUrl + "/chat/room/delete", request, Object.class);
                log.info("delete chatting room response -> {}", response);
                break;
            } catch (Exception e) {
                log.info("EventBridge : chatting server disconnect");
            }
            retry++;
        }


        jsonObject.put("prefix", StreamUtils.getSubtitleUrl(dto.getDetail().getRecording_s3_key_prefix()));
        request = new HttpEntity<>(jsonObject.toJSONString(), headers);

        retry = 1;
        while (retry < 4) {
            try {
                ResponseEntity<Object> response = restTemplate.postForEntity(subtitleUrl + "/end", request, Object.class);
                streamMapper.saveSubtitleKey(dto.getDetail().getStream_id(), channel.getChatKey());
                log.info("save subtitle : {}", channel.getChatKey());
                if (response.getStatusCodeValue()==200) break;
            } catch (Exception e) {
                log.info("Subtitle server error : {}", channel.getChatKey());
            }
            retry++;
        }



        String newChatKey = StreamUtils.generateChatKey();
        streamMapper.changeChatKey(channel.getChannelId(), newChatKey);
    }

    @Transactional
    public void startRecordingEvent(EventBridgeDto.Request dto) {
        String channelArn = dto.getResources()
                .stream()
                .filter(v -> v.contains("channel"))
                .findFirst()
                .orElseThrow(() -> new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND));

        ChannelResponse channel = channelMapper.findChannelByChannelArn(channelArn)
                .orElseThrow(() -> new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND));

        if (streamMapper.isExistStreamId(dto.getDetail().getStream_id())) {
            throw new StreamException(ResponseCodeEnum.FAILED, "stream_id is already exist");
        }

        Long vodId = streamMapper.isExistInternalStream(channelArn);

        if (vodId == null) {
            streamMapper.deleteExternalFlagX(channelArn);

            EventBridgeDto.StartRecording startRecordingEventDto = EventBridgeDto.StartRecording.builder()
                    .channelId(channel.getChannelId())
                    .playbackUrl(channel.getPlaybackUrl())
                    .userNo(channel.getUserNo())
                    .streamId(dto.getDetail().getStream_id())
                    .vodTitle(channel.getChannelName() + "'s live streaming")
                    .vodDetail(channel.getChannelName() + "'s live streaming")
                    .startTime(dto.getTime())
                    .prefix(dto.getDetail().getRecording_s3_key_prefix())
                    .transmittedType(TransmittedType.EXTERNAL.getValue())
                    .build();
            streamMapper.createStartRecordingEvent(startRecordingEventDto);
        } else {
            dto.setVodId(vodId);
            streamMapper.updateStartRecordingEvent(dto);
        }
    }

    public StreamResponse.Lookup lookupStream(Long userNo) {

        return streamMapper.lookupStream(userNo)
                .orElseThrow(() -> new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND));
    }

    public StreamResponse.Detail getStream(
            JwtUser user,
            Long vodId,
            StreamType streamType
    ) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        StreamResponse.Detail detail = streamMapper.getStream(
                user != null ? user.getUserNo() : null,
                vodId,
                streamType.getValue()
        ).orElseThrow(() -> new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND));

        StreamDto.VodUrls vodUrls = getVodUrls(detail.getVodPrefix());
        if (vodUrls == null) {
            StreamDto.VodUrls defaultUrl = new StreamDto.VodUrls();
            defaultUrl.setAuto(detail.getPlaybackUrl());

            defaultUrl.set_360p30(detail.getPlaybackUrl().replace("/720p/", "/360p/"));
            defaultUrl.set_480p30(detail.getPlaybackUrl().replace("/720p/", "/480p/"));
            defaultUrl.set_720p30(detail.getPlaybackUrl());

            detail.setVodUrls(defaultUrl);
        } else {
            detail.setVodUrls(vodUrls);
        }

        if (streamType.equals(StreamType.LIVE)) {
            detail.setThumbnail(StreamUtils.getThumbnailEnd(cloudFrontUrl, detail.getVodPrefix(), detail.getStreamLength()));
        } else {
            System.out.println(detail.getThumbnail());
            if (detail.getThumbnail() == null) {
                detail.setThumbnail(StreamUtils.getThumbnailStart(cloudFrontUrl, detail.getVodPrefix()));
            }
        }

        if (detail.getSubtitleKey() != null) {
            detail.setCaptions(StreamUtils.getSubtitleUrls(cloudFrontUrl, detail.getVodPrefix(), detail.getSubtitleKey()));
        }

        return detail;
    }

    private StreamDto.VodUrls getVodUrls(String prefix) {
        String recordingDataUrl = StreamUtils.getRecordingDataUrl(cloudFrontUrl, prefix);

        VodResponse response = null;
        try {
            response = restTemplate.getForObject(recordingDataUrl, VodResponse.class);
        } catch (Exception e) {
            log.info("recording_start.json not found => {}", prefix);
        }
        return response != null ? StreamUtils.getVodUrl(cloudFrontUrl, prefix, response) : null;
    }

    @Transactional
    public Integer addViewCount(Long vodId) {
        streamMapper.addViewCount(vodId);
        return streamMapper.getViewCount(vodId);
    }

    @Transactional
    public int deleteInternalFlagX(Long userNo) {
        return streamMapper.deleteInternalFlagX(userNo);
    }

    @Transactional
    public boolean deleteVod(Long userNo, Long vodId) {
        verifyVodOwner(userNo, vodId);

        streamMapper.deleteVod(vodId);

        return true;
    }

    @Transactional
    public boolean displayToggle(Long userNo, Long vodId) {
        verifyVodOwner(userNo, vodId);

        streamMapper.displayToggle(vodId);
        return true;
    }

    private void verifyVodOwner(Long userNo, Long vodId) {
        Long owner = streamMapper.getVodOwner(vodId)
                .orElseThrow(() -> new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND, "vod not found = " + vodId));

        if (!owner.equals(userNo)) {
            throw new StreamException(ResponseCodeEnum.ACCESS_DENIED);
        }
    }

    @Transactional
    public StreamResponse.AfterWatchVideo toggleAfterWatchVideo(Long userNo, Long vodId, StreamRequest.After request) {
        boolean isPresent = streamMapper.isPresentAfterWatchVideo(userNo, vodId);
        Boolean isSave = request != null ? request.getIsSave() : null;

        if (isPresent) {
            if (isSave == null || !isSave) {
                streamMapper.deleteAfterWatchVideo(userNo, vodId);
            }
        } else {
            if (isSave == null || isSave)
            streamMapper.insertAfterWatchVideo(userNo, vodId);
        }
        return StreamResponse.AfterWatchVideo.builder()
                .saveToggle(isSave == null ? !isPresent : isSave)
                .vodId(vodId)
                .build();
    }

    public StreamResponse.List getWatchedVideoList(StreamRequest.WatchedVideoList request) {
        request.setOffset(request.getPageNo()*request.getPageSize());
        List<StreamResponse> afterWatchVideoList = streamMapper.getWatchedVideoList(request);

        afterWatchVideoList.stream().filter(StreamResponse::getIsLive).forEach(stream -> {
            stream.setThumbnail(StreamUtils.getThumbnailEnd(cloudFrontUrl, stream.getPrefix(), stream.getStreamTime()));
            stream.setViewCount(getUserCount(stream.getChatKey()));
        });

        Integer totalElements = streamMapper.getTotalWatchedStreams(request.getUserNo(), request.getSearch(), request.getType());

        return StreamResponse.List.builder()
                .streams(afterWatchVideoList)
                .pageNo(request.getPageNo())
                .pageSize(request.getPageSize())
                .totalElements(totalElements)
                .build();
    }

    public StreamResponse.Ready getStreamReadyToken(JwtUser user) {
        String salt = SHA256.salt();
        String expired = String.valueOf(System.currentTimeMillis() + 1000 * 60 * 5);
        String origin = StreamUtils.getStreamReadyTokenOrigin(user, salt, expired, readyKey);
        String hash = SHA256.encrypt(origin);

        Base64.Encoder encoder = Base64.getEncoder().withoutPadding();
        String encodedSalt = encoder.encodeToString(salt.getBytes());
        String encodedExpired = encoder.encodeToString(expired.getBytes());
        String encodedHash = encoder.encodeToString(hash.getBytes());

        return StreamResponse.Ready.builder()
                .token(encodedExpired + "." + encodedSalt + "." + encodedHash)
                .build();
    }

    public boolean verifyStreamReadyToken(JwtUser user, StreamRequest.Ready readyState) {
        String[] split = readyState.getToken().split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();

        String expired= new String(decoder.decode(split[0]));
        String salt = new String(decoder.decode(split[1]));
        String hash = new String(decoder.decode(split[2]));

        String origin = StreamUtils.getStreamReadyTokenOrigin(user, salt, expired, readyKey);

        if (Long.parseLong(expired) - System.currentTimeMillis() < 0) {
            return false;
        }

        return SHA256.verify(origin, hash);
    }

    public Object getChannelStreamList(Long channelId, StreamRequest.MyList request) {

        if (request.getOrderType().equals(VideoOrderType.TOP1)) {
            return streamMapper.getChannelTop1Stream(channelId, request.getUserNo());
        } else {
            String hashKey = redisCacheManager.listCacheRedisHashKey(channelId, request.getOrderType().getType(), request.getPageNo(), request.getPageSize());

            if (request.getPageNo().equals(0)) {
                StreamResponse.List cacheList = redisCacheManager.getHashType(RedisKey.USER_CHANNEL_STREAM_LIST, hashKey, StreamResponse.List.class);
                if (cacheList != null) {
                    return cacheList;
                }
            }

            request.setOffset(request.getPageNo()*request.getPageSize());

            List<StreamResponse> streamList = streamMapper.getChannelStreamList(channelId, request);

            int totalElements = streamMapper.getChannelTotalStreams(channelId, request.getSearch());

            StreamResponse.List result = StreamResponse.List.builder()
                    .streams(streamList)
                    .pageNo(request.getPageNo())
                    .pageSize(request.getPageSize())
                    .totalElements(totalElements)
                    .build();

            if (request.getPageNo().equals(0)) {
                redisCacheManager.setHashType(RedisKey.USER_CHANNEL_STREAM_LIST, hashKey, result);
            }

            return result;
        }
    }
}

















