package com.meitalk.api.model.stream.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum VideoOrderType {
    TOP1("Viewed Top1", "T"),
    NEWEST("Newest first", "N"),
    EARLIEST("Earliest first", "E"),
    LIKED("Most Liked", "L"),
    VIEWED("Most Viewed", "V"),
    COMMENTS("Top comments", "C");

    private final String value;
    private final String type;

    VideoOrderType(String value, String type) {
        this.value = value;
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
