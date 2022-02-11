package com.meitalk.api.controller;

import com.meitalk.api.common.annotation.JwtAuthentication;
import com.meitalk.api.model.LikeRequest;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PatchMapping("/{content-id}/{type}")
    public ResponseWithData toggleLike(@JwtAuthentication JwtUser user,
                                       @PathVariable("content-id") Long contentId,
                                       @PathVariable("type") String type,
                                       @RequestBody(required = false) LikeRequest request) {
        return ResponseWithData.success(likeService.toggleLike(user.getUserNo(),contentId,type, request));
    }
}
