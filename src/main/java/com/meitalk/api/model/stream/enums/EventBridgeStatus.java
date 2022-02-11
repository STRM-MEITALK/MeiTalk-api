package com.meitalk.api.model.stream.enums;

import lombok.Getter;

@Getter
public enum EventBridgeStatus {
    STREAM_START("Stream Start"),
    STREAM_END("Stream End"),
    STREAM_FAILURE("Stream Failure"),

    STARVATION_START("Starvation Start"),
    STARVATION_END("Starvation End"),

    RECORDING_END("Recording End"),
    RECORDING_START("Recording Start");

    private String value;

    EventBridgeStatus(String value) {
        this.value = value;
    }
}
