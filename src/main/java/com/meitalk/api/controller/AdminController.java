package com.meitalk.api.controller;

import com.meitalk.api.common.annotation.JwtAuthentication;
import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.exception.StreamException;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/{vodId}/end")
    public ResponseWithData endStreamingAdmin(
            @JwtAuthentication JwtUser user,
            @PathVariable Long vodId
    ) {
        if (user.getRole().equals("ADMIN")) {
            adminService.endStreaming(vodId);
            return ResponseWithData.success().message("Stop Stream");
        }
        throw new StreamException(ResponseCodeEnum.ACCESS_DENIED);
    }
}
