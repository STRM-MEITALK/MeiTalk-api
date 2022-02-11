package com.meitalk.api.utils;

import com.meitalk.api.exception.ResponseCodeEnum;
import com.meitalk.api.exception.StreamException;
import com.meitalk.api.model.stream.StreamDto;

import java.security.MessageDigest;
import java.util.Objects;
import java.util.UUID;

public class SHA256 {

    public static String encrypt(String origin) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(origin.getBytes());
            return bytesToHex(md.digest());
        } catch (Exception e) {
            throw new StreamException(ResponseCodeEnum.FAILED, e.getMessage());
        }
    }

    public static boolean verify(String origin, String token) {
        return Objects.equals(encrypt(origin), token);
    }

    public static String salt() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 15);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }


    public static StreamDto.ReadyToken parse(String token) {
        try {
            return StreamDto.ReadyToken.builder()
                    .salt(token.substring(0, 16))
                    .hash(token.substring(16))
                    .build();
        } catch (Exception e) {
            throw new StreamException(ResponseCodeEnum.UNSUPPORTED_TOKEN);
        }
    }
}
