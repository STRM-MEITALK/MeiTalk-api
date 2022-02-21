package com.meitalk.api.service;

import com.meitalk.api.common.enums.ListTypeEnum;
import com.meitalk.api.common.enums.ProposalType;
import com.meitalk.api.exception.ProposalException;
import com.meitalk.api.mapper.ProposalMapper;
import com.meitalk.api.model.proposal.ProposalDto;
import com.meitalk.api.model.user.JwtUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.meitalk.api.exception.ResponseCodeEnum.FAILED;
import static com.meitalk.api.exception.ResponseCodeEnum.RESULT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProposalService {
    private final ProposalMapper proposalMapper;

    public void createProposal(JwtUser user, ProposalDto.CreateProposal dto) {
        final Optional<ProposalDto> proposal = proposalMapper.getProposalUserNo(user.getUserNo());

        if (proposal.isPresent()) {
            checkProposalStatus(proposal.get().getType());
        }
        proposalMapper.createProposal(dto);
    }

    private void checkProposalStatus(ProposalType type) {
        if (type == ProposalType.PROGRESS || type == ProposalType.SALE) {
            throw new ProposalException(FAILED, "already proposal");
        }
    }

    public List<ProposalDto.ListProposal> getProposalList(ListTypeEnum type) {
        return proposalMapper.getProposalList(type);
    }

    public ProposalDto getProposalDetail(Long id) {
        return proposalMapper.getProposalDetail(id)
                .orElseThrow(() -> new ProposalException(RESULT_NOT_FOUND));
    }

    private boolean matchUser(Long proposalUserNo, Long userNo) {
        if (proposalUserNo.equals(userNo))
                return true;
        return false;
    }

    public void updateProposal(JwtUser user, ProposalDto.CreateProposal updateProposalDto) {
        if (!matchUser(user.getUserNo(), updateProposalDto.getUserNo())) {
            throw new ProposalException(FAILED, "not match user");
        }

        proposalMapper.updateProposal(updateProposalDto);
    }

    public ProposalDto.SimpleProposal getProposalSimple(JwtUser user) {
        return proposalMapper.getProposalSimple(user.getUserNo())
                .orElseThrow(() -> new ProposalException(RESULT_NOT_FOUND));
    }
}
