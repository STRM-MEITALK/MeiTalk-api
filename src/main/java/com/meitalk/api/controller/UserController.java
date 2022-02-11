package com.meitalk.api.controller;

import com.meitalk.api.common.annotation.JwtAuthentication;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService profileService;

    @PostMapping("/image")
    public ResponseWithData userImageUpload(
            @JwtAuthentication JwtUser user,
            @RequestParam("images")MultipartFile multipartFile
    ) throws IOException {
        profileService.imageChange(user, multipartFile);
        return ResponseWithData.success();
    }

    @PutMapping("/name/{newName}")
    public ResponseWithData updateUserName(
            @JwtAuthentication JwtUser user,
            @PathVariable String newName
    ) {
        profileService.updateName(user, newName);
        return ResponseWithData.success();
    }
}
