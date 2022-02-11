package com.meitalk.api.service;

import com.meitalk.api.exception.StreamException;
import com.meitalk.api.mapper.AdminMapper;
import com.meitalk.api.model.stream.aws.IvsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.meitalk.api.exception.ResponseCodeEnum.RESULT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminMapper adminMapper;
    private final MediaServer mediaServer;

    public void endStreaming(Long vodId) {
        String channelArn = adminMapper.findChannelArnByVodId(vodId)
                .orElseThrow(() -> new StreamException(RESULT_NOT_FOUND));

        log.info("channelArn == {}", channelArn);
        IvsResponse.StopStream stopStreamResult = mediaServer.stopStream(channelArn);
        log.info("StopStreamResult == {}", stopStreamResult);
    }
}
