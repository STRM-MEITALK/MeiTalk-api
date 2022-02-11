package com.meitalk.api.mapper;

import com.meitalk.api.model.stream.StreamDto;
import com.meitalk.api.model.stream.StreamRequest;
import com.meitalk.api.model.stream.StreamResponse;
import com.meitalk.api.model.stream.aws.EventBridgeDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Mapper
public interface StreamMapper {

    Integer saveVodInfo(StreamDto.SaveInfo saveStreamInfo);

    List<StreamResponse> getStreamList(StreamRequest.List type);

    void updateStream(StreamRequest.Update dto);

    Integer getTotalStreams(String flag, Long vodId);

    int endRecordingEvent(EventBridgeDto.Request dto);

    void updateStartRecordingEvent(EventBridgeDto.Request dto);

    Long isExistInternalStream(String channelArn);

    void createStartRecordingEvent(EventBridgeDto.StartRecording dto);

    Optional<StreamResponse.Lookup> lookupStream(Long userNo);

    Optional<StreamResponse.Detail> getStream(Long userNo, Long vodId, String type);

    Long findChannelIdByVodId(Long vodId);

    Optional<String> findChannelArnByChannelId(Long channelId);

    void addViewCount(Long vodId);

    int deleteInternalFlagX(Long userNo);

    int deleteExternalFlagX(String channelArn);

    Integer getViewCount(Long vodId);

    Optional<Long> getVodOwner(Long vodId);

    void deleteVod(Long vodId);

    void displayToggle(Long vodId);

    void startStreamEvent(String streamId);

    EventBridgeDto.StartStream startStream(String streamId);

    void changeChatKey(Long channelId, String newChatKey);

    void insertAfterWatchVideo(Long userNo, Long vodId);

    void deleteAfterWatchVideo(Long userNo, Long vodId);

    boolean isPresentAfterWatchVideo(Long userNo, Long vodId);

    List<StreamResponse> getWatchedVideoList(StreamRequest.WatchedVideoList request);

    Integer getTotalWatchedStreams(Long userNo, String search, String type);

    int saveSubtitleKey(String streamId, String subtitleKey);

    boolean isExistStreamId(String streamId);

    void updateDisplayFlag(Long vodId);

    List<StreamResponse> getChannelStreamList(Long channelId, StreamRequest.MyList request);

    int getChannelTotalStreams(Long channelId, String search);

    StreamResponse getChannelTop1Stream(Long channelId, Long userNo);
}
