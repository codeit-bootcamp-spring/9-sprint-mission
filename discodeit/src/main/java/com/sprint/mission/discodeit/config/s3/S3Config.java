package com.sprint.mission.discodeit.config.s3;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3Config {
  private final AwsProperties props;
  public S3Config(AwsProperties props) { this.props = props; }

  @Bean
  public S3Client s3Client() {
    // .env에서 키가 주입된 경우: 정적 자격 증명 사용
    if (props.getAccessKey() != null && !props.getAccessKey().isBlank()) {
      return S3Client.builder()
          .region(Region.of(props.getRegion()))
          .credentialsProvider(
              StaticCredentialsProvider.create(
                  AwsBasicCredentials.create(
                      props.getAccessKey(),
                      props.getSecretKey()
                  )
              )
          )
          .build();
    }
    // 그렇지 않으면: 기본 체인(환경변수, 프로파일, IAM Role)을 자동 탐색
    return S3Client.builder()
        .region(Region.of(props.getRegion()))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }
}
