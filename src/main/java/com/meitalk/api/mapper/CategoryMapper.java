package com.meitalk.api.mapper;

import com.meitalk.api.model.stream.CategoryResponse;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CategoryMapper {
    List<CategoryResponse> getCategoryList(String langCd);
}
