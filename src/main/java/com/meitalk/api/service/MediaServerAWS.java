package com.meitalk.api.service;

import com.meitalk.api.config.AmazonIVSConfig;
import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.exception.StreamException;
import com.meitalk.api.model.stream.ChannelRequest;
import com.meitalk.api.model.stream.aws.IvsResponse;
import com.amazonaws.services.ivs.AmazonIVS;
import com.amazonaws.services.ivs.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServerAWS implements MediaServer {

    private final AmazonIVSConfig amazonIVSConfig;
    private final AmazonIVS amazonIVS;

    @Override
    public IvsResponse.CreateChannel createChannel(String channelName) {
        log.info("aws service : createChannel");
        CreateChannelRequest request = new CreateChannelRequest();
        request.setAuthorized(true);
        request.setName(channelName);
        request.setRecordingConfigurationArn(amazonIVSConfig.getRecordingArn());

        CreateChannelResult result = amazonIVS.createChannel(request);

        return IvsResponse.CreateChannel.builder()
                .channelArn(result.getChannel().getArn())
                .channelName(result.getChannel().getName())
                .playbackUrl(result.getChannel().getPlaybackUrl())
                .streamArn(result.getStreamKey().getArn())
                .streamKey(result.getStreamKey().getValue())
                .build();
    }

    @Override
    public IvsResponse.CreateStreamKey createStreamKey(String channelArn) {
        CreateStreamKeyRequest request = new CreateStreamKeyRequest();
        request.setChannelArn(channelArn);

        CreateStreamKeyResult result;
        try {
            result = amazonIVS.createStreamKey(request);
        } catch (ServiceQuotaExceededException e) {
            throw new StreamException(ResponseCodeEnum.FAILED, "StreamKey is already exist");
        }
        log.info("createStreamKey result = {}", result);
        return IvsResponse.CreateStreamKey.builder()
                    .streamKey(result.getStreamKey().getValue())
                    .streamArn(result.getStreamKey().getArn())
                    .build();
    }

    @Override
    public IvsResponse.StopStream stopStream(String channelArn) {
        StopStreamRequest request = new StopStreamRequest();
        request.setChannelArn(channelArn);
        StopStreamResult result = amazonIVS.stopStream(request);
        log.info("result = {}", result);
        return IvsResponse.StopStream.builder()
                .isStopped(true)
                .build();
    }

    @Override
    public IvsResponse.DeleteChannel deleteChannel(String arn) {
        DeleteChannelRequest request = new DeleteChannelRequest();
        request.setArn(arn);

        DeleteChannelResult result;
        try {
            result = amazonIVS.deleteChannel(request);
        } catch (Exception e) {
            throw new StreamException(ResponseCodeEnum.FAILED, "Fail : delete channel");
        }
        return IvsResponse.DeleteChannel.builder()
                .isDeleted(true)
                .build();
    }

    @Override
    public DeleteStreamKeyResult deleteStreamKey(String arn) {
        DeleteStreamKeyRequest request = new DeleteStreamKeyRequest();
        request.setArn(arn);

        DeleteStreamKeyResult result;
        try {
            result = amazonIVS.deleteStreamKey(request);
        } catch (Exception e) {
            throw new StreamException(ResponseCodeEnum.FAILED, "Fail : delete stream key");
        }
        return result;
    }

    @Override
    public ListChannelsResult listChannels(String filterByName, int maxResults) {
        ListChannelsRequest request = new ListChannelsRequest();
        request.setMaxResults(maxResults);
        ListChannelsResult result = amazonIVS.listChannels(request);
        log.info("result = {}", result);
        return result;
    }

    @Override
    public ListStreamKeysResult listStreamKeys(String channelArn, int maxResults) {
        ListStreamKeysRequest request = new ListStreamKeysRequest();
        request.setChannelArn(channelArn);
        request.setMaxResults(maxResults);

        ListStreamKeysResult result = amazonIVS.listStreamKeys(request);
        log.info("result = {}", result);
        return result;
    }

    @Override
    public IvsResponse.GetStream getStream(String channelArn) {
        GetStreamRequest request = new GetStreamRequest();
        request.setChannelArn(channelArn);

        GetStreamResult result = amazonIVS.getStream(request);
        log.info("result = {}", result);
        return IvsResponse.GetStream.builder()
                .channelArn(result.getStream().getChannelArn())
                .health(result.getStream().getHealth())
                .playbackUrl(result.getStream().getPlaybackUrl())
                .startTime(result.getStream().getStartTime())
                .state(result.getStream().getState())
                .streamId(result.getStream().getStreamId())
                .viewerCount(result.getStream().getViewerCount())
                .build();
    }

    @Override
    public IvsResponse.UpdateChannel updateChannel(ChannelRequest.Update reqUpdateChannel) {
        UpdateChannelRequest request = new UpdateChannelRequest();
        request.setArn(reqUpdateChannel.getChannelArn());
        request.setAuthorized(reqUpdateChannel.getAuthorized());
        request.setLatencyMode(reqUpdateChannel.getLatencyMode());
        request.setName(reqUpdateChannel.getName());
        request.setRecordingConfigurationArn(reqUpdateChannel.getRecordingConfigurationArn());
        request.setType(reqUpdateChannel.getType());

        UpdateChannelResult result = amazonIVS.updateChannel(request);
        System.out.println("result -> " + result);

        Channel channel = result.getChannel();
        return IvsResponse.UpdateChannel.builder()
                .channelArn(channel.getArn())
                .authorized(channel.getAuthorized())
                .ingestEndpoint(channel.getIngestEndpoint())
                .latencyMode(channel.getLatencyMode())
                .name(channel.getName())
                .recordingConfigurationArn(channel.getRecordingConfigurationArn())
                .tags(channel.getTags())
                .type(channel.getType())
                .build();
    }

    @Override
    public GetStreamSessionResult getStreamSession(String arn) {
        GetStreamSessionRequest request = new GetStreamSessionRequest();
        request.setChannelArn(arn);

        GetStreamSessionResult result = amazonIVS.getStreamSession(request);
        log.info("result = {}", result);
        return result;
    }

    @Override
    public GetStreamSessionResult getStreamSession(String arn, String streamId) {
        GetStreamSessionRequest request = new GetStreamSessionRequest();
        request.setChannelArn(arn);
        request.setStreamId(streamId);

        GetStreamSessionResult result = amazonIVS.getStreamSession(request);
        log.info("result = {}", result);
        return result;
    }

    @Override
    public GetStreamKeyResult getStreamKey(String arn) {
        GetStreamKeyRequest request = new GetStreamKeyRequest();
        request.setArn(arn);

        GetStreamKeyResult result = amazonIVS.getStreamKey(request);
        log.info("result = {}", result);
        return result;
    }


}
