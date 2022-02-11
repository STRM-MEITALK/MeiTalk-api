package com.meitalk.api.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Mapper
public interface AdminMapper {
    Optional<String> findChannelArnByVodId(Long vodId);
}
