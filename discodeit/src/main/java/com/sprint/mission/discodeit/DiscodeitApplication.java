package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@EnableCaching
@SpringBootApplication
public class DiscodeitApplication {

  public static void main(String[] args) {
    SpringApplication.run(DiscodeitApplication.class, args);
  }
}
