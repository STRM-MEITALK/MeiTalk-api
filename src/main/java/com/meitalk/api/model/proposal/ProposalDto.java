package com.meitalk.api.model.proposal;

import com.meitalk.api.common.enums.ProposalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
public class ProposalDto {
    private Long id;
    private Long userNo;
    private String title;
    private ProposalType type;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProposal {
        private String title;
        private Long userNo;
    }

    @Builder
    @Getter
    public static class ListProposal {
        private Long id;
        private String title;
    }

    @Builder
    @Getter
    public static class SimpleProposal {
        private Long id;
        private String title;
    }
}
