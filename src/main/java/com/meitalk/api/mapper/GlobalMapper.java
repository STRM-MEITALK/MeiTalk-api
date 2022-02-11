package com.meitalk.api.mapper;

import com.meitalk.api.model.global.AuthEmailVerificationDto;
import com.meitalk.api.model.global.ResCountryCode;
import com.meitalk.api.model.global.UpdateEmailVerificationTableDto;
import com.meitalk.api.model.user.UserProfile;
import com.meitalk.api.model.user.UserProfileWithEmailVerification;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GlobalMapper {

    List<ResCountryCode> selectAllCountryCode();

    int updateUserEmailVerificationByUserMail(String userMail, String verifyFlag);

    UserProfile selectUserByUserMail(String userMail);

    int insertAuthEmailVerification(AuthEmailVerificationDto dto);

    UserProfileWithEmailVerification selectUserByUserMailKey(String userKey);

    int updateEmailVerificationTable(UpdateEmailVerificationTableDto dto);
}
