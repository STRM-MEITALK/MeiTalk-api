package com.meitalk.api.service;

import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.mapper.GlobalMapper;
import com.meitalk.api.model.ResponseBuilder;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.global.AuthEmailVerificationDto;
import com.meitalk.api.model.global.ReqSendAuthMail;
import com.meitalk.api.model.global.SendAuthMailDto;
import com.meitalk.api.model.global.UpdateEmailVerificationTableDto;
import com.meitalk.api.model.user.UserProfile;
import com.meitalk.api.model.user.UserProfileWithEmailVerification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class GlobalService {

    private final GlobalMapper globalMapper;
    private final EmailService emailService;

    public ResponseWithData getAllCountryCode() {
        return ResponseWithData.builder()
                .response(ResponseBuilder.builder()
                        .output(ResponseCodeEnum.SUCCESS.getCode())
                        .result(ResponseCodeEnum.SUCCESS.getMessage())
                        .build())
                .data(globalMapper.selectAllCountryCode())
                .build();
    }

    public ResponseWithData authEmailVerification(ReqSendAuthMail req) throws IOException, MessagingException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        UserProfile user = globalMapper.selectUserByUserMail(req.getUserMail());
        if (user == null) {
            log.info("not found user");
            return ResponseWithData.builder()
                    .response(ResponseBuilder.builder()
                            .output(-1)
                            .result("not found user")
                            .build())
                    .data(null)
                    .build();
        }
        if (user.getEmailVerification().equalsIgnoreCase("Y")){
            log.info("already email verification y");
            return ResponseWithData.builder()
                    .response(ResponseBuilder.builder()
                            .output(-2)
                            .result("already email verification y")
                            .build())
                    .data(null)
                    .build();
        }
        UpdateEmailVerificationTableDto dto = UpdateEmailVerificationTableDto.builder()
                .userNo(user.getUserNo())
                .verification("E")
                .build();
        globalMapper.updateEmailVerificationTable(dto);
        String userKey = UUID.randomUUID().toString();
        AuthEmailVerificationDto authEmailVerificationDto = AuthEmailVerificationDto.builder()
                .userId(user.getUserNo())
                .userKey(userKey)
                .build();
        int insertSuccess = globalMapper.insertAuthEmailVerification(authEmailVerificationDto);
        if (insertSuccess == 0) {
            return ResponseWithData.builder()
                    .response(ResponseBuilder.builder()
                            .output(-3)
                            .result("database error")
                            .build())
                    .data(null)
                    .build();
        }
        emailService.sendAuthEmail(SendAuthMailDto.builder()
                .userMail(req.getUserMail())
                .userKey(userKey)
                .build());
        return ResponseWithData.builder()
                .response(ResponseBuilder.builder()
                        .output(ResponseCodeEnum.SUCCESS.getCode())
                        .result(ResponseCodeEnum.SUCCESS.getMessage())
                        .build())
                .data(null)
                .build();
    }

    @Transactional
    public int authMailVerifyCheck(String userKey) {
        UserProfileWithEmailVerification userProfile = globalMapper.selectUserByUserMailKey(userKey);
        log.info(userProfile.toString());
        if (userProfile.getUserProfile() == null) {
            log.info("not found user");
            return -1;
        }
        if (userProfile.getUserProfile().getEmailVerification().equalsIgnoreCase("Y")){
            log.info("already y");
            return 1;
        }
        if (userProfile.getUserEmailVerification().getVerification().equalsIgnoreCase("E")){
            log.info("expired mail");
            return 2;
        }
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime createTime = LocalDateTime.parse(userProfile.getUserEmailVerification().getVerificationCreateTime(), inputFormat);
        LocalDateTime expiredTime = createTime.plusDays(1);
        LocalDateTime now = LocalDateTime.now();
        int updateEmailVerify;
        if (expiredTime.isAfter(now)) {
            updateEmailVerify = globalMapper.updateUserEmailVerificationByUserMail(userProfile.getUserProfile().getMailId(), "Y");
            UpdateEmailVerificationTableDto dto = UpdateEmailVerificationTableDto.builder()
                    .userNo(userProfile.getUserProfile().getUserNo())
                    .verification("Y")
                    .build();
            System.out.println(dto);
            globalMapper.updateEmailVerificationTable(dto);
            if (updateEmailVerify == 1) {
                log.info("update y");
                return 1;
            }
        } else {
            return 2;
        }
        log.info("db error");
        return -2;
    }

}
