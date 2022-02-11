package com.meitalk.api.mapper;

import com.meitalk.api.model.wallet.ReqWallet;
import com.meitalk.api.model.wallet.ResultWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface WalletMapper {

    int insertWalletExternalTransaction(
            @Param("req") ReqWallet.ExternalTransaction req
    );

    ResultWallet.WalletUserInfo selectWalletUserInfoByReceiver(
            @Param("receiver") String receiver,
            @Param("coinType") String coinType
    );

    String selectWalletUserAmountByUserNoAndCoinType(
            @Param("userNo") Long userNo,
            @Param("coinType") String coinType
    );

    int updateWalletUserAmount(
            @Param("userNo") Long userNo,
            @Param("coinType") String coinType,
            @Param("updateAmount") String updateAmount
    );

}
