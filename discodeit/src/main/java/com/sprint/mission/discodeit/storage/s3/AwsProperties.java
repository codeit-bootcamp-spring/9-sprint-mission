package com.sprint.mission.discodeit.storage.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//@EnableConfigurationProperties(AwsProperties.class)

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "discodeit.storage.s3")
public class AwsProperties {

  private String accessKey;
  private String secretKey;
  private String region;
  private String bucket;

}