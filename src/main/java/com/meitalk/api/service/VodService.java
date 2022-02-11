package com.meitalk.api.service;

import com.meitalk.api.exception.StreamException;
import com.meitalk.api.mapper.VodMapper;
import com.meitalk.api.model.vod.VodDto;
import com.meitalk.api.model.vod.VodRequest;
import com.meitalk.api.exception.ResponseCodeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class VodService {

    private final VodMapper vodMapper;
    private final S3Uploader s3Uploader;
    RestTemplate restTemplate = new RestTemplate();

    @Value("${chat.url}")
    private String chatUrl;

    @Value("${aws.ivs.cloudFrontUrl}")
    private String cloudFrontUrl;

    @Transactional
    public void saveRealDetailVod(String chatKey, VodRequest.UpdateDetail dto) {
        Long channelId = vodMapper.getChannelIdByChatKey(chatKey)
                .orElseThrow(() -> new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND));
        dto.setChannelId(channelId);
        log.info("channel_id == {}", channelId);

        vodMapper.updateVod(dto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("chatKey", chatKey);
        jsonObject.put("title", dto.getTitle());
        jsonObject.put("description", dto.getDescription());
        jsonObject.put("category", dto.getCategory().getNum());
        log.info("jsonObject -> {}", jsonObject);

        HttpEntity<String> request = new HttpEntity<>(jsonObject.toJSONString(), headers);
        Object response = restTemplate.postForEntity(
                chatUrl + "/chat/room/realtime/info", request, Object.class);
        log.info("socket server response -> {}", response);
    }

    @Transactional(isolation= Isolation.REPEATABLE_READ)
    public void updateVodThumbnail(
            Long userNo,
            Long vodId,
            MultipartFile multipartFile
    ) throws IOException {
        VodDto.GetThumbnailUrl getThumbnailUrlDto = vodMapper.getThumbnailUrl(vodId, userNo)
                .orElseThrow(() -> new StreamException(ResponseCodeEnum.RESULT_NOT_FOUND));

        String thumbnailPath = getThumbnailUrlDto.getVodPrefix() + "/media/thumbnails";

        VodDto.UploadImage uploadImageDto = s3Uploader.upload(multipartFile, thumbnailPath);
        log.info("uploadImageDto -> {}", uploadImageDto.getName());

        String[] splitName = uploadImageDto.getName().split("/");
        String thumbnailUrl = cloudFrontUrl;
        for(int i=3; i<13; i++) {
            thumbnailUrl += "/" +splitName[i];
        }

        log.info("thumbnailUrl -> {}", thumbnailUrl);
        vodMapper.saveThumbnailPath(thumbnailUrl, vodId);
    }
}