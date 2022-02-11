package com.meitalk.api.service;

import com.meitalk.api.mapper.TestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestService {

    private final TestMapper testMapper;

    public List<String> getTestList() {
        return testMapper.getTestList();
    }
}
