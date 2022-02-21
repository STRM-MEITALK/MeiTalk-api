package com.meitalk.api.mapper;

import com.meitalk.api.common.enums.ListTypeEnum;
import com.meitalk.api.model.proposal.ProposalDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Mapper
@Repository
public interface ProposalMapper {

    void createProposal(ProposalDto.CreateProposal createProposalDto);

    Optional<ProposalDto> getProposalUserNo(Long userNo);

    List<ProposalDto.ListProposal> getProposalList(ListTypeEnum type);

    Optional<ProposalDto> getProposalDetail(Long id);

    void updateProposal(ProposalDto.CreateProposal updateProposalDto);

    Optional<ProposalDto.SimpleProposal> getProposalSimple(Long id);
}
