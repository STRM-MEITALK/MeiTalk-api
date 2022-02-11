package com.meitalk.api.service;

import com.meitalk.api.common.enums.LanguageCode;
import com.meitalk.api.mapper.CategoryMapper;
import com.meitalk.api.model.stream.CategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getCategoryList(LanguageCode langCd) {
        return categoryMapper.getCategoryList(langCd == null ? LanguageCode.EN.getValue() : langCd.getValue());
    }
}
