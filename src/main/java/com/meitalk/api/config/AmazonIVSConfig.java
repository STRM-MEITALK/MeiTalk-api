package com.meitalk.api.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ivs.AmazonIVS;
import com.amazonaws.services.ivs.AmazonIVSClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Amazon IVS Connection
 */
@Getter
@Configuration
public class AmazonIVSConfig {

    @Value("${aws.ivs.accessKey}")
    private String accessKey;

    @Value("${aws.ivs.secretKey}")
    private String secretKey;

    @Value("${aws.ivs.region}")
    private String ivsRegion;

    @Value("${aws.ivs.recording.arn}")
    private String recordingArn;

    @Value("${aws.ivs.rtmpUrl}")
    private String rtmpUrl;

    @Bean
    public AmazonIVS amazonIvs() {
        AWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(basicAWSCredentials);
        return AmazonIVSClient.builder().withCredentials(provider).withRegion(ivsRegion).build();
    }
}
