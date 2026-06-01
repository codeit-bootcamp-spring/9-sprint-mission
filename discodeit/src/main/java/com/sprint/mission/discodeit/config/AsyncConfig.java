package com.sprint.mission.discodeit.config;

import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

  private final MdcSecurityContextTaskDecorator taskDecorator;

  @Bean
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setTaskDecorator(taskDecorator);
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(25);
    executor.setThreadNamePrefix("async-");
    executor.initialize();
    return executor;
  }
}
