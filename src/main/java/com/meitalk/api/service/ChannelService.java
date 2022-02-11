package com.meitalk.api.service;

import com.meitalk.api.config.AmazonIVSConfig;
import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.exception.StreamException;
import com.meitalk.api.mapper.ChannelMapper;
import com.meitalk.api.model.ResponseBuilder;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.stream.ChannelDto;
import com.meitalk.api.model.stream.ChannelRequest;
import com.meitalk.api.model.stream.ChannelResponse;
import com.meitalk.api.model.stream.aws.IvsResponse;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.model.vod.VodDto;
import com.meitalk.api.utils.StreamUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static com.meitalk.api.exception.ResponseCodeEnum.ACCESS_DENIED;
import static com.meitalk.api.exception.ResponseCodeEnum.RESULT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelService {

    private final MediaServer mediaServer;
    private final ChannelMapper channelMapper;
    private final AmazonIVSConfig amazonIVSConfig;
    private final PlayToken playToken;
    private final S3Uploader s3Uploader;


    @Value("${aws.s3.channel.banner.path}")
    public String profilePath;

    @Transactional
    public ChannelResponse.Create createIvsChannel(
            JwtUser user, ChannelRequest.Create dto
    ) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        boolean isChannelResult = channelMapper.isExistChannel(user.getUserNo());

        IvsResponse.CreateChannel createChannelRes;
        String streamKey;
        String playbackUrl;
        String playTokenValue;

        if (!isChannelResult) {
            createChannelRes = mediaServer.createChannel(String.valueOf(user.getUserNo()));
            streamKey = createChannelRes.getStreamKey();
            playbackUrl = createChannelRes.getPlaybackUrl();

            String chatKey = StreamUtils.generateChatKey();
            log.info("chat key == {}", chatKey);

            playTokenValue =
                    playToken.issuePlayToken(createChannelRes.getChannelArn());

            ChannelDto.Save saveChannelDto = ChannelDto.Save.builder()
                    .userNo(user.getUserNo())
                    .channelArn(createChannelRes.getChannelArn())
                    .playbackUrl(createChannelRes.getPlaybackUrl())
                    .streamKey(createChannelRes.getStreamKey())
                    .streamArn(createChannelRes.getStreamArn())
                    .chatKey(chatKey)
                    .playToken(playTokenValue)
                    .build();
            channelMapper.saveChannel(saveChannelDto);
        } else {
            ChannelResponse.Create result = channelMapper.findUserChannelValue(user.getUserNo());
            log.info("findUserChannel == {}", result);
            streamKey = result.getStreamValue();
            playbackUrl = result.getPlaybackUrl();
            playTokenValue = result.getPlayToken();
        }

        return ChannelResponse.Create.builder()
                .rtmpUrl(amazonIVSConfig.getRtmpUrl())
                .streamValue(streamKey)
                .playbackUrl(playbackUrl)
                .playToken(playTokenValue)
                .build();
    }

    @Transactional
    public void deleteChannel(JwtUser user, Long channelId) {
        ChannelResponse channel = verifyChannelOwner(user.getUserNo(), channelId);
        IvsResponse.DeleteChannel deleteChannelResult = mediaServer.deleteChannel(channel.getChannelArn());
        channelMapper.deleteChannel(channel.getChannelId());
    }

    @Transactional
    public void deleteStreamKey(JwtUser user, Long channelId) {
        ChannelResponse channel = verifyChannelOwner(user.getUserNo(), channelId);

        mediaServer.deleteStreamKey(channel.getStreamArn());
        channelMapper.deleteStreamKey(channel.getChannelId());
    }

    @Transactional
    public void issueStreamKey(JwtUser user, Long channelId) {
        ChannelResponse channel = verifyChannelOwner(user.getUserNo(), channelId);

        IvsResponse.CreateStreamKey streamKey = mediaServer.createStreamKey(channel.getChannelArn());
        ChannelDto.ReissueStreamKey request = ChannelDto.ReissueStreamKey.builder()
                .channelId(channelId)
                .streamKey(streamKey.getStreamKey())
                .streamArn(streamKey.getStreamArn())
                .build();
        channelMapper.reissueStreamKey(request);
    }

    @Transactional
    public void reissueStreamKey(JwtUser user, Long channelId) {
        ChannelResponse channel = verifyChannelOwner(user.getUserNo(), channelId);
        log.info("find channel -> {}", channel);

        mediaServer.deleteStreamKey(channel.getStreamArn());

        IvsResponse.CreateStreamKey streamKey = mediaServer.createStreamKey(channel.getChannelArn());
        log.info("streamKey -> {}", streamKey);

        ChannelDto.ReissueStreamKey request = ChannelDto.ReissueStreamKey.builder()
                .channelId(channelId)
                .streamKey(streamKey.getStreamKey())
                .streamArn(streamKey.getStreamArn())
                .build();
        channelMapper.reissueStreamKey(request);
    }

    public IvsResponse.UpdateChannel updateChannel(Long userNo, Long channelId, ChannelRequest.Update reqUpdateChannel) {

        ChannelResponse channel = verifyChannelOwner(userNo, channelId);

        reqUpdateChannel.setChannelArn(channel.getChannelArn());
        return mediaServer.updateChannel(reqUpdateChannel);
    }

    private ChannelResponse verifyChannelOwner(Long userNo, Long channelId) {
        ChannelResponse channel = channelMapper.findChannelByChannelId(channelId)
                .orElseThrow(() -> new StreamException(RESULT_NOT_FOUND));
        if (!channel.getUserNo().equals(userNo))
            throw new StreamException(ACCESS_DENIED);
        return channel;
    }

    public ResponseWithData createInternalChannel(ChannelRequest.CreateInternalChannel req) {
        int insertSuccess = channelMapper.insertMyChannel(req.getUserNo());
        if (insertSuccess != 1) {
            return ResponseWithData.failed(ResponseCodeEnum.DATABASE_ERROR);
        }
        return ResponseWithData.success();
    }

    public ResponseWithData getChannel(JwtUser user, Long channelId) {
        ChannelResponse.ChannelDetail channelDetail = channelMapper.findChannelDetailByChannelId(channelId);
        if (channelDetail == null) {
            return ResponseWithData.failed(RESULT_NOT_FOUND);
        }
        if (user == null || user.getUserNo() != channelDetail.getUserProfile().getUserNo()) {
            channelDetail.setIsMe("N");
        } else if (user.getUserNo() == channelDetail.getUserProfile().getUserNo()) {
            channelDetail.setIsMe("Y");
        }
        return ResponseWithData.success(channelDetail);
    }


    @Transactional
    public ResponseWithData channelBannerUpload(Long userNo, Long channelId, MultipartFile profileImage) throws IOException {
        ChannelResponse channel = verifyChannelOwner(userNo, channelId);

        VodDto.UploadImage upload = s3Uploader.upload(profileImage, profilePath);

        if (channel.getChannelImage() != null) {
            s3Uploader.deleteFile(channel.getChannelImage());
        }

        int updateSuccess = channelMapper.updateChannelBannerImage(channel.getChannelId(), upload.getUrl());

        if (updateSuccess != 1) {
            return ResponseWithData.failed(ResponseCodeEnum.DATABASE_ERROR);
        }

        return ResponseWithData.success(upload.getUrl());
    }


    @Transactional
    public ResponseWithData channelBannerDelete(Long userNo, Long channelId) throws IOException {
        ChannelResponse channel = verifyChannelOwner(userNo, channelId);

        if (channel.getChannelImage() != null) {
            s3Uploader.deleteFile(channel.getChannelImage());
        }

        int updateSuccess = channelMapper.updateChannelBannerImage(channel.getChannelId(), null);

        if (updateSuccess != 1) {
            return ResponseWithData.failed(ResponseCodeEnum.DATABASE_ERROR);
        }

        return ResponseWithData.success();
    }

    public ResponseWithData updateChannelDetail(JwtUser user, Long channelId, ChannelRequest.ChannelUpdate req) {
        ChannelResponse.ChannelDetail channelDetail = channelMapper.findChannelDetailByChannelId(channelId);
        if (channelDetail == null) {
            return ResponseWithData.failed(RESULT_NOT_FOUND);
        }
        if (channelDetail.getUserProfile().getUserNo() != user.getUserNo()) {
            return ResponseWithData.builder()
                    .data(null)
                    .response(ResponseBuilder.builder()
                            .output(-1)
                            .result("not owner of the channel")
                            .build())
                    .build();
        }
        int updateSuccess = channelMapper.updateChannelDetailByChannelId(channelId, req);
        if (updateSuccess != 1) {
            return ResponseWithData.failed(ResponseCodeEnum.DATABASE_ERROR);
        }
        return ResponseWithData.success(ResponseCodeEnum.SUCCESS);
    }
}
