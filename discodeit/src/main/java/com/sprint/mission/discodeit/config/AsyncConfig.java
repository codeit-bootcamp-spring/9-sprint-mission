package com.sprint.mission.discodeit.config;

import java.util.Map;
import java.util.concurrent.Executor;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableAsync
@ConditionalOnProperty(name = "discodeit.async.enabled", havingValue = "true", matchIfMissing = true)
public class AsyncConfig {

  @Bean
  public Executor taskExecutor(TaskDecorator taskDecorator) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("discodeit-async-");
    executor.setTaskDecorator(taskDecorator);
    executor.initialize();
    return executor;
  }

  @Bean
  public TaskDecorator contextPropagatingTaskDecorator() {
    return task -> {
      Map<String, String> mdcContextMap = MDC.getCopyOfContextMap();
      SecurityContext securityContext = SecurityContextHolder.getContext();

      return () -> {
        Map<String, String> previousMdcContextMap = MDC.getCopyOfContextMap();
        SecurityContext previousSecurityContext = SecurityContextHolder.getContext();
        try {
          if (mdcContextMap == null) {
            MDC.clear();
          } else {
            MDC.setContextMap(mdcContextMap);
          }
          SecurityContextHolder.setContext(securityContext);
          task.run();
        } finally {
          if (previousMdcContextMap == null) {
            MDC.clear();
          } else {
            MDC.setContextMap(previousMdcContextMap);
          }
          SecurityContextHolder.setContext(previousSecurityContext);
        }
      };
    };
  }
}
