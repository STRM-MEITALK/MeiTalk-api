package com.meitalk.api.controller;

import com.meitalk.api.common.annotation.JwtAuthentication;
import com.meitalk.api.common.enums.ListTypeEnum;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.proposal.ProposalDto;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.service.ProposalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/proposal")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    @PostMapping
    public ResponseWithData createProposal(
            @RequestBody ProposalDto.CreateProposal dto,
            @JwtAuthentication JwtUser user
    ) {
        proposalService.createProposal(user, dto);
        return ResponseWithData.success();
    }

    @GetMapping("/list/{type}")
    public ResponseWithData getProposalAllList(
            @PathVariable final ListTypeEnum type
    ) {
        return ResponseWithData.success(proposalService.getProposalList(type));
    }

    @GetMapping("/{proposalId}")
    public ResponseWithData getProposalDetail(
            @PathVariable final Long proposalId
    ) {
        return ResponseWithData.success(proposalService.getProposalDetail(proposalId));
    }

    @PatchMapping("/{proposalId}")
    public ResponseWithData updateProposal(
            @PathVariable final Long proposalId,
            @JwtAuthentication JwtUser user
    ) {
        return ResponseWithData.success(proposalService.getProposalDetail(proposalId));
    }

    @PatchMapping("/simple")
    public ResponseWithData getProposalSimple(
            @JwtAuthentication JwtUser user
    ) {
        return ResponseWithData.success(proposalService.getProposalSimple(user));
    }
}
