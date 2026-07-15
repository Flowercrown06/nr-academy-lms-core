package com.nracademy.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${aws.default-region:eu-north-1}")
    private String defaultRegion;

    @Bean
    S3Client s3Client() {
        return S3Client.builder()
            .region(Region.of(defaultRegion))
            .credentialsProvider(DefaultCredentialsProvider.builder().build())
            .build();
    }
}