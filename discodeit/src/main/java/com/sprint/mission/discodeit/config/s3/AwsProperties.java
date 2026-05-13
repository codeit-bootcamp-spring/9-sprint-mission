package com.sprint.mission.discodeit.config.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "discodeit.storage.s3")
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class AwsProperties {

  private String accessKey;
  private String secretKey;
  private String region;
  private String bucket;

}