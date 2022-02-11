package com.meitalk.api.service;

import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.mapper.WalletMapper;
import com.meitalk.api.model.ResponseBuilder;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.wallet.ReqWallet;
import com.meitalk.api.model.wallet.ResultWallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletMapper walletMapper;

    public BigDecimal setScale8Floor(BigDecimal number) {
        return number.setScale(8, RoundingMode.FLOOR);
    }

    /*
        Wallet external transaction insert.
        Wallet user amount update.
     */
    public ResponseWithData walletExternalTransaction(ReqWallet.ExternalTransaction req) {
        log.info("Wallet external transaction : {}", req);
        req.setAmount(setScale8Floor(new BigDecimal(req.getAmount())).toPlainString());

        int insertSuccess = walletMapper.insertWalletExternalTransaction(req);
        if (insertSuccess != 1) {
            return ResponseWithData.failed(ResponseCodeEnum.DATABASE_ERROR);
        }
        log.info("wallet external transaction insert success");

        ResultWallet.WalletUserInfo walletUserInfo = walletMapper.selectWalletUserInfoByReceiver(req.getReceiver(), req.getCoinType());
        if (walletUserInfo == null) {
            log.warn("Not found receiver user : {}", req);
            return ResponseWithData.builder()
                    .response(ResponseBuilder.builder()
                            .output(-1)
                            .result("Not found receiver user.")
                            .build())
                    .data(null)
                    .build();
        }
        log.info("wallet user info (external transaction) : {}", walletUserInfo);

        String walletUserAmount = walletMapper.selectWalletUserAmountByUserNoAndCoinType(walletUserInfo.getUserNo(), req.getCoinType());
        BigDecimal currentAmount = new BigDecimal(walletUserAmount);
        BigDecimal requestAmount = new BigDecimal(req.getAmount());
        BigDecimal updateAmount = setScale8Floor(currentAmount.add(requestAmount));
        int updateSuccess = walletMapper.updateWalletUserAmount(walletUserInfo.getUserNo(), req.getCoinType(), updateAmount.toPlainString());
        if (updateSuccess != 1) {
            return ResponseWithData.failed(ResponseCodeEnum.DATABASE_ERROR);
        }
        log.info("wallet user amount update success : {} -> {}", walletUserAmount, updateAmount);

        return ResponseWithData.success();
    }

}
