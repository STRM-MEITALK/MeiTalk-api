package com.meitalk.api.common;

import com.meitalk.api.common.enums.LanguageCode;
import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.exception.StreamException;
import com.meitalk.api.model.stream.enums.VideoOrderType;
import com.meitalk.api.model.stream.enums.StreamType;
import com.meitalk.api.model.stream.enums.WatchedMenuType;
import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter {

    public static class Stream implements Converter<String, StreamType> {
        @Override
        public StreamType convert(String source) {
            return StreamType.valueOf(source.toUpperCase());
        }
    }

    public static class VideoOrder implements Converter<String, VideoOrderType> {
        @Override
        public VideoOrderType convert(String source) {
            return java.util.stream.Stream.of(VideoOrderType.values())
                    .filter(v -> v.getType().equals(source.toUpperCase()))
                    .findFirst()
                    .orElseThrow(() -> new StreamException(ResponseCodeEnum.INVALID_PARAMETER, "Type is not valid"));
        }
    }

    public static class WatchedMenu implements Converter<String, WatchedMenuType> {
        @Override
        public WatchedMenuType convert(String source) {
            return WatchedMenuType.valueOf(source.toUpperCase());
        }
    }

    public static class Language implements Converter<String, LanguageCode> {
        @Override
        public LanguageCode convert(String source) {
            return LanguageCode.valueOf(source.toUpperCase());
        }
    }
}


