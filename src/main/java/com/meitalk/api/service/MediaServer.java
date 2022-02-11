package com.meitalk.api.service;

import com.meitalk.api.model.stream.ChannelRequest;
import com.meitalk.api.model.stream.aws.IvsResponse;
import com.amazonaws.services.ivs.model.*;

public interface MediaServer {

    IvsResponse.CreateChannel createChannel(String channelName);
    IvsResponse.StopStream stopStream(String channelArn);
    IvsResponse.DeleteChannel deleteChannel(String arn);
    IvsResponse.CreateStreamKey createStreamKey(String channelArn);
    IvsResponse.GetStream getStream(String channelArn);
    IvsResponse.UpdateChannel updateChannel(ChannelRequest.Update reqUpdateChannel);

    DeleteStreamKeyResult deleteStreamKey(String channelId);
    ListChannelsResult listChannels(String filterByName, int maxResults);
    ListStreamKeysResult listStreamKeys(String channelArn, int maxResults);
    GetStreamSessionResult getStreamSession(String arn);
    GetStreamSessionResult getStreamSession(String arn, String streamId);
    GetStreamKeyResult getStreamKey(String arn);

}
