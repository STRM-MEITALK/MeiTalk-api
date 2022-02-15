package com.meitalk.api.controller;

import com.meitalk.api.common.annotation.JwtAuthentication;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.model.wallet.ReqWallet;
import com.meitalk.api.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseWithData createUserWallet(@JwtAuthentication JwtUser user) {
        return walletService.createUserWallet(user);
    }

    @PostMapping("/external/transaction")
    public ResponseWithData walletExternalTransaction(@RequestBody ReqWallet.ExternalTransaction req) {
        return walletService.walletExternalTransaction(req);
    }

}

