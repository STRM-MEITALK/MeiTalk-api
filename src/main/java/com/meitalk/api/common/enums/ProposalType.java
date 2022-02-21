package com.meitalk.api.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProposalType {
    SALE("X"),
    PROGRESS("Y"),
    FINISHED("N")
    ;

    private final String proposalType;
}
