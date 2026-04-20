package com.sprint.mission.discodeit.storage.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3Config {

  private final S3Properties properties;

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .region(Region.of(properties.getRegion()))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    properties.getAccessKey(),
                    properties.getSecretKey()
                )
            )
        )
        .build();
  }
}