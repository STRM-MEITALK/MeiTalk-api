package com.meitalk.api.controller;

import com.meitalk.api.common.annotation.RedisCacheEvict;
import com.meitalk.api.mapper.TestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final TestMapper testMapper;

    @GetMapping
    public String healthCheck() {
        return "Meitalk api server is available!";
    }

    @GetMapping("/streams")
    public String getSimpleStreams() {
        testMapper.getSimpleStreams();
        return "success";
    }

    @GetMapping("/streams/limit")
    public String getSimpleStreamsLimit() {
        testMapper.getSimpleStreamsLimit();
        return "success";
    }

    @GetMapping("/streams/{vodId}")
    public String getSimpleStreamById(@PathVariable Long vodId) {
        testMapper.getSimpleStreamById(vodId);
        return "success";
    }

    @GetMapping("/streams/user/{userNo}")
    public String getSimpleStreamByUserNo(@PathVariable Long userNo) {
        testMapper.getSimpleStreamByUserNo(userNo);
        return "success";
    }

    @RedisCacheEvict
    @GetMapping("/del")
    public String deleteCache() {
        return "success";
    }
}
