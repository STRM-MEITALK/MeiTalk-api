package com.meitalk.api.service;

import com.meitalk.api.mapper.UserMapper;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.model.vod.VodDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final S3Uploader s3Uploader;
    private final UserMapper userMapper;

    @Value("${aws.s3.profile.path}")
    public String profilePath;

    @Transactional(isolation= Isolation.REPEATABLE_READ)
    public void imageChange(
            JwtUser user,
            MultipartFile multipartFile
    ) throws IOException {
        VodDto.UploadImage upload = s3Uploader.upload(multipartFile, profilePath);

        userMapper.changeProfile(user.getUserNo(), upload.getUrl());
    }

    @Transactional(isolation= Isolation.REPEATABLE_READ)
    public void updateName(JwtUser user, String newName) {
        userMapper.updateName(user.getUserNo(), newName);
    }
}
