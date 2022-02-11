package com.meitalk.api.service;

import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.exception.StreamException;
import com.meitalk.api.mapper.StudioMapper;
import com.meitalk.api.model.TypeEnum;
import com.meitalk.api.model.comment.CommentRequest;
import com.meitalk.api.model.comment.CommentResponse;
import com.meitalk.api.model.stream.StreamDto;
import com.meitalk.api.model.stream.StreamRequest;
import com.meitalk.api.model.stream.StreamResponse;
import com.meitalk.api.model.stream.aws.VodResponse;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.utils.StreamUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudioService {

    private final StudioMapper studioMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${aws.ivs.cloudFrontUrl}")
    private String cloudFrontUrl;

    public StreamResponse.List getUserVodList(StreamRequest.MyList request) {

        request.setOffset(request.getPageNo()*request.getPageSize());

        List<StreamResponse> streamList = studioMapper.getMyStreamList(request);

        Integer totalElements = studioMapper.getMyTotalStreams(request.getUserNo(), request.getIsPublic(), request.getSearch());

        return StreamResponse.List.builder()
                .streams(streamList)
                .pageNo(request.getPageNo())
                .pageSize(request.getPageSize())
                .totalElements(totalElements)
                .build();
    }

    public StreamResponse.Detail getUserVodDetail(JwtUser user, Long vodId) {
        StreamResponse.Detail detail = studioMapper.getUserVodDetail(user.getUserNo(), vodId)
                .orElseThrow(() -> new StreamException(ResponseCodeEnum.ACCESS_DENIED));

        detail.setVodUrls(getVodUrls(detail.getVodPrefix()));

        detail.setThumbnail(StreamUtils.getThumbnailStart(cloudFrontUrl, detail.getVodPrefix()));

        if (detail.getSubtitleKey() != null) {
            detail.setCaptions(StreamUtils.getSubtitleUrls(cloudFrontUrl, detail.getVodPrefix(), detail.getSubtitleKey()));
        }
        return detail;
    }

    private StreamDto.VodUrls getVodUrls(String prefix) {
        String recordingDataUrl = StreamUtils.getRecordingDataUrl(cloudFrontUrl, prefix);
        VodResponse response = restTemplate.getForObject(recordingDataUrl, VodResponse.class);
        return response != null ? StreamUtils.getVodUrl(cloudFrontUrl, prefix, response) : null;
    }

    @Transactional
    public CommentResponse.Delete deleteMyVodComment(Long userNo, CommentRequest request, Long id) {
        TypeEnum type = TypeEnum.findByParam(request.getType());

        Long commentId = null;
        Long replyId = null;

        if(type==null) {
            throw new StreamException(ResponseCodeEnum.INVALID_PARAMETER);
        } else if (type.equals(TypeEnum.COMMENT)) {
            commentId = id;
        } else if (type.equals(TypeEnum.COMMENT_REPLY)) {
            commentId = studioMapper.getCommentIdByReplyId(id);
            replyId = id;
        }

        Long owner = studioMapper.getVodOwner(commentId)
                .orElseThrow(() -> new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND));

        if (!owner.equals(userNo)) {
            throw new StreamException(ResponseCodeEnum.ACCESS_DENIED);
        }

        int queryCount = studioMapper.deleteComment(userNo,id,type.getDbCode());

        if(queryCount!=1){
            throw new StreamException(ResponseCodeEnum.QUERY_FAILED);
        }
        return CommentResponse.Delete.builder()
                .commentId(commentId)
                .replyId(replyId)
                .type(request.getType())
                .build();
    }
}
