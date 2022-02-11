package com.meitalk.api.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper {

    void changeProfile(Long userNo, String imageUrl);

    void updateName(Long userNo, String newName);
}
