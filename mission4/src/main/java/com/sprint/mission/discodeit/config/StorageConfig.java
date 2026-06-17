package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.local.LocalBinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
public class StorageConfig {

  @Bean(name = "binaryContentStorage")
  @ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local", matchIfMissing = true)
  public BinaryContentStorage localBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") String rootPath) {
    return new LocalBinaryContentStorage(Paths.get(rootPath));
  }

  @Bean
  @ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
  public BinaryContentStorage s3BinaryContentStorage(
      @Value("${s3.access-key}") String accessKey,
      @Value("${s3.secret-key}") String secretKey,
      @Value("${s3.region}") String region,
      @Value("${s3.bucket}") String bucket) {
    return new S3BinaryContentStorage(accessKey, secretKey, region, bucket);
  }
}