package com.meitalk.api.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

@Slf4j
@Component
public class PlayToken {

    @Value("${playTokenKey.path}")
    private String playTokenKey;

    @Value("${play-token.access-allow.url}")
    private String playTokenUrl;

    public String issuePlayToken(String channelArn) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        PemReader reader = new PemReader(new FileReader(playTokenKey));
        PemObject pemObject = reader.readPemObject();
        byte[] content = pemObject.getContent();

        KeyFactory instance = KeyFactory.getInstance("EC");
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(content);
        PrivateKey privateKey = instance.generatePrivate(privateKeySpec);

        //issue play token
        String jwtToken = Jwts.builder()
                .claim("aws:channel-arn", channelArn)
                .claim("aws:access-control-allow-origin", playTokenUrl)
                .claim("exp", System.currentTimeMillis() + 90000000000000L )
                .signWith(privateKey, SignatureAlgorithm.ES384)
                .compact();
        log.info("jwt -> {}", jwtToken);

        return jwtToken;
    }
}
