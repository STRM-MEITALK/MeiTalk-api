package com.meitalk.api.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TypeEnum {
    VOD("vod","V"),
    COMMENT("comment","C"),
    COMMENT_REPLY("reply","R"),
    COMMUNITY_COMMENT("communityComment","CC"),
    COMMUNITY_COMMENT_REPLY("communityReply","CR");
    private final String param;
    private final String dbCode;

    TypeEnum(String param, String dbCode) {
        this.param = param;
        this.dbCode = dbCode;
    }

    public static TypeEnum findByParam(String input){
        return Arrays.stream(values()).filter( o -> o.param.equals(input)).findFirst().orElse(null);
    }
}
