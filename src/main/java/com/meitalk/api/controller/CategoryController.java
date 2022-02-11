package com.meitalk.api.controller;

import com.meitalk.api.common.enums.LanguageCode;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseWithData getCategoryList(@RequestParam(required = false) LanguageCode langCd) {
        return ResponseWithData.success(categoryService.getCategoryList(langCd));
    }
}
