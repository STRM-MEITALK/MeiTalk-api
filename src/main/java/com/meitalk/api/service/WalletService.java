package com.meitalk.api.service;

import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.mapper.WalletMapper;
import com.meitalk.api.model.ResponseBuilder;
import com.meitalk.api.model.ResponseWithData;
import com.meitalk.api.model.user.JwtUser;
import com.meitalk.api.model.wallet.DtoWallet;
import com.meitalk.api.model.wallet.ReqWallet;
import com.meitalk.api.model.wallet.ResultWallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    @Value("${wallet.server.url}")
    private String walletServerUrl;
    @Value("${wallet.create.uri}")
    private String createWalletUri;

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

    public ResponseWithData createUserWallet(JwtUser user) {
        DtoWallet.CreateWalletRequestDto requestDto = DtoWallet.CreateWalletRequestDto.builder()
                .email(user.getEmail())
                .password(user.getEmail())
                .build();
        WebClient webClient = WebClient.builder()
                .baseUrl(walletServerUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        DtoWallet.CreateWalletResponseDto response = webClient.post()
                .uri(createWalletUri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), DtoWallet.CreateWalletRequestDto.class)
                .retrieve()
                .bodyToMono(DtoWallet.CreateWalletResponseDto.class)
                .block();
        log.info("create wallet server response. : {}", response);
        if (!Boolean.parseBoolean(response.getSuccess())) {
            log.info("create wallet fail. : {}", response);
            return ResponseWithData.failed(ResponseCodeEnum.FAILED);
        }
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        DtoWallet.CreateWalletResponseDto response = restTemplate.postForObject(walletServerUrl + createWalletUri, requestDto, DtoWallet.CreateWalletResponseDto.class);

        DtoWallet.CreateWalletDto createWalletDto = DtoWallet.CreateWalletDto.builder()
                .userNo(user.getUserNo())
                .userEmail(user.getEmail())
                .BTC(response.getData().getBTC())
                .ETH(response.getData().getETH())
                .BNB(response.getData().getETH())
                .STRM(response.getData().getETH())
                .ABBC(null)
                .build();
        int successInsert = walletMapper.insertWalletUserInfo(createWalletDto);
        if (successInsert == 0) {
            log.error("DB error");
            return ResponseWithData.failed(ResponseCodeEnum.DATABASE_ERROR);
        }

        DtoWallet.UserAmount userAmountDto = DtoWallet.UserAmount.builder()
                .userNo(user.getUserNo())
                .userEmail(user.getEmail())
                .BTC("0.0")
                .ETH("0.0")
                .BNB("0.0")
                .STRM("0.0")
                .ABBC("0.0")
                .build();
        int successInsert2 = walletMapper.insertWalletUserAmount(userAmountDto);
        if (successInsert2 == 0) {
            log.error("DB error");
            return ResponseWithData.failed(ResponseCodeEnum.DATABASE_ERROR);
        }

        return ResponseWithData.success(createWalletDto);
    }
}
