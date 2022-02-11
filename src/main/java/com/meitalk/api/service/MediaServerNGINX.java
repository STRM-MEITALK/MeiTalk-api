package com.meitalk.api.service;

import com.meitalk.api.mapper.ChannelMapper;
import com.meitalk.api.mapper.StreamMapper;
import com.meitalk.api.model.stream.ChannelRequest;
import com.meitalk.api.model.stream.aws.IvsResponse;
import com.amazonaws.services.ivs.model.*;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class MediaServerNGINX implements MediaServer {

    private final ChannelMapper channelMapper;
    private final StreamMapper streamMapper;

    @Override
    public IvsResponse.CreateChannel createChannel(String channelName) {
        String channelArn = UUID.randomUUID().toString().replaceAll("-", "");
        String streamKey = UUID.randomUUID().toString().replaceAll("-", "");
        return null;
    }

    @Override
    public IvsResponse.StopStream stopStream(String channelArn) {
        return null;
    }

    @Override
    public IvsResponse.DeleteChannel deleteChannel(String arn) {
        return null;
    }

    @Override
    public IvsResponse.CreateStreamKey createStreamKey(String channelArn) {
//        int channel = channelMapper.isExistChannel(channelArn);
//        if (channel == 0) {
            String streamKey = UUID.randomUUID().toString().replaceAll("-", "");
//        }
        return null;
    }

    @Override
    public IvsResponse.GetStream getStream(String channelArn) {
        return null;
    }

    @Override
    public IvsResponse.UpdateChannel updateChannel(ChannelRequest.Update reqUpdateChannel) {
        return null;
    }

    @Override
    public DeleteStreamKeyResult deleteStreamKey(String channelId) {
        return null;
    }

    @Override
    public ListChannelsResult listChannels(String filterByName, int maxResults) {
        return null;
    }

    @Override
    public ListStreamKeysResult listStreamKeys(String channelArn, int maxResults) {
        return null;
    }

    @Override
    public GetStreamSessionResult getStreamSession(String arn) {
        return null;
    }

    @Override
    public GetStreamSessionResult getStreamSession(String arn, String streamId) {
        return null;
    }

    @Override
    public GetStreamKeyResult getStreamKey(String arn) {
        return null;
    }
}
