package com.sprint.mission.discodeit.config.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws.s3")
public class AwsProperties {

  private String accessKey;
  private String secretKey;
  private String region;
  private String bucket;

}