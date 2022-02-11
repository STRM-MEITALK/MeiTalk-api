package com.meitalk.api.utils;

import com.meitalk.api.model.stream.StreamDto;
import com.meitalk.api.model.stream.aws.VodResponse;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.common.enums.LanguageCode;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class StreamUtils {

    public static String getStreamReadyTokenOrigin(JwtUser user, String salt, String expired, String readyKey) {
        return user.getUserNo() + salt + expired + readyKey;
    }

    public static String getRecordingDataUrl(String cloudFrontUrl, String prefix) {
        return cloudFrontUrl + prefix.replace("ivs/v1/880047505389", "") + "/events/recording-started.json";
    }

    public static String getSubtitleUrl(String prefix) {
        return prefix + "/media";
    }

    public static String getVodUrlAuto(String cloudFrontUrl, String prefix) {
        return cloudFrontUrl + prefix.replace("ivs/v1/880047505389", "") + "/media/hls/master.m3u8";
    }
    public static String getVodUrl360(String cloudFrontUrl, String prefix) {
        return cloudFrontUrl + prefix.replace("ivs/v1/880047505389", "") + "/media/hls/360p30/playlist.m3u8";
    }

    public static StreamDto.SubtitleUrls getSubtitleUrls(String cloudFrontUrl, String prefix, String subtitleKey) {
        String s = cloudFrontUrl + prefix.replace("ivs/v1/880047505389", "") + "/media/subtitle/" + subtitleKey;
        return StreamDto.SubtitleUrls.builder()
                .ja(s + LanguageCode.JA.getExtensions())
                .en(s + LanguageCode.EN.getExtensions())
                .ru(s + LanguageCode.RU.getExtensions())
                .ko(s + LanguageCode.KO.getExtensions())
                .zh(s + LanguageCode.ZH.getExtensions())
                .id(s + LanguageCode.ID.getExtensions())
                .build();
    }

    public static StreamDto.VodUrls getVodUrl(String cloudFrontUrl, String prefix, VodResponse vodResponse) {
        String vodPrefix = cloudFrontUrl + prefix.replace("ivs/v1/880047505389", "") + "/media/hls";

        VodResponse.Media.Hls hls = vodResponse.getMedia().getHls();

        StreamDto.VodUrls vodUrls = new StreamDto.VodUrls();
        vodUrls.setAuto(vodPrefix + "/" + hls.getPlaylist());

        vodResponse.getMedia().getHls().getRenditions().forEach(rendition -> {
            try {
                String path = rendition.getPath();
                vodUrls.getClass().getDeclaredMethod("set_" + path, String.class).invoke(vodUrls, vodPrefix + "/" + path + "/" + rendition.getPlaylist());
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return vodUrls;
    }

    public static String getThumbnailEnd(String cloudFrontUrl, String prefix, String duration) {
        if (prefix == null || duration == null) return null;

        String[] split = duration.split(":");
        int thumbnailIndex = 0;
        if (split.length == 3) {
            thumbnailIndex = Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
        } else if (split.length == 2) {
            thumbnailIndex = Integer.parseInt(split[0]);
        }
        thumbnailIndex = Math.max(thumbnailIndex - 10, 0);

        return cloudFrontUrl + prefix.replace("ivs/v1/880047505389", "") + "/media/thumbnails/thumb"+ thumbnailIndex + ".jpg";
    }

    public static String getThumbnailStart(String cloudFrontUrl, String prefix) {
        return cloudFrontUrl + prefix.replace("ivs/v1/880047505389", "") + "/media/thumbnails/thumb0.jpg";
    }

    public static String generateChatKey() {
        return UUID.randomUUID().toString();
    }
}
