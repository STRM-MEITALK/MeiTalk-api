package com.meitalk.api.mapper;

import com.meitalk.api.model.stream.StreamResponse;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TestMapper {
    List<String> getTestList();

    void update(Long vodId, String thumbnail);

    List<StreamResponse> getSimpleStreams();

    List<StreamResponse> getSimpleStreamsLimit();

    List<StreamResponse> getSimpleStreamById(Long vodId);

    List<StreamResponse> getSimpleStreamByUserNo(Long userNo);
}
