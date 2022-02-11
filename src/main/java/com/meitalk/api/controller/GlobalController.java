package com.meitalk.api.controller;

import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.global.ReqSendAuthMail;
import com.meitalk.api.service.EmailService;
import com.meitalk.api.service.GlobalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

@Slf4j
@RequestMapping("/api/global")
@RequiredArgsConstructor
@RestController
public class GlobalController {

    private final GlobalService globalService;
    private final EmailService emailService;
    @Value("${react.base.uri}")
    String reactBaseUri;
    @Value("${email.auth.success.uri}")
    String emailAuthSuccessUri;
    @Value("${email.auth.expired.uri}")
    String emailAuthExpiredUri;
    @Value("${email.auth.fail.uri}")
    String emailAuthFailUri;

    @PostMapping("/auth-mail")
    public ResponseWithData sendAuthMail(@RequestBody ReqSendAuthMail req) throws IOException, MessagingException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        return globalService.authEmailVerification(req);
    }

    @GetMapping("/auth-mail/success/{userKey}")
    public void authMailVerifySuccessRedirect(@PathVariable String userKey, @RequestParam String mailId,
                                              HttpServletResponse response) throws IOException, ParseException {
        int success = globalService.authMailVerifyCheck(userKey);
        String redirectUrl = "";
        System.out.println(success);
        if (success == 1) {
//            1 (already y, success mail)
            redirectUrl = emailAuthSuccessUri;
        } else if (success == 2) {
//            2 (duplicate mail, expired mail)
            redirectUrl = emailAuthExpiredUri;
        } else {
//            -1, -2 (user not found, db update error)
            redirectUrl = emailAuthFailUri;
        }
        response.sendRedirect(redirectUrl + "?user=" + mailId);
    }

    @GetMapping("/country-code")
    public ResponseWithData allCountryCode() {
        return globalService.getAllCountryCode();
    }

}
