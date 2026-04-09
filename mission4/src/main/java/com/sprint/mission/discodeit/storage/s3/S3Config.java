package com.sprint.mission.discodeit.storage.s3;


import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class S3Config {

  @Value("${discodeit.storage.s3.access-key}")
  private String accessKey;
  @Value("${discodeit.storage.s3.secret-key}")
  private String secretKey;
  @Value("${discodeit.storage.s3.region}")
  private String region;
  @Value("${discodeit.storage.s3.bucket}")
  private String bucket;


  @Bean
  @Primary
  @ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
  public BinaryContentStorage binaryContentStorage() {
    return new S3BinaryContentStorage(accessKey, secretKey, region, bucket);
  }


}
