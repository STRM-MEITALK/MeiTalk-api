package com.meitalk.api.controller;

import com.meitalk.api.common.annotation.JwtAuthentication;
import com.meitalk.api.common.enums.Role;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.comment.CommentRequest;
import com.meitalk.api.model.stream.StreamRequest;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.service.StudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/studio")
@RequiredArgsConstructor
public class StudioController {

    private final StudioService studioService;

    @GetMapping("/streams")
    public ResponseWithData getUserVodList(@JwtAuthentication(role = Role.STREAMER) JwtUser user,
                                           StreamRequest.MyList request) {
        request.setUserNo(user.getUserNo());
        return ResponseWithData.success(studioService.getUserVodList(request));
    }

    @GetMapping("/streams/{vodId}")
    public ResponseWithData getUserVodDetail(@JwtAuthentication(role = Role.STREAMER) JwtUser user,
                                             @PathVariable Long vodId) {
        return ResponseWithData.success(studioService.getUserVodDetail(user, vodId));
    }

    @DeleteMapping("/comment/{id}")
    public ResponseWithData deleteMyVodComment(@JwtAuthentication(role = Role.STREAMER) JwtUser user,
                                               CommentRequest request,
                                               @PathVariable Long id) {
        return ResponseWithData.success(studioService.deleteMyVodComment(user.getUserNo(), request, id));
    }
}
