package com.sprint.mission.discodeit.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig {

  @Bean("taskExecutor")
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(50);
    executor.setQueueCapacity(200);
    executor.setThreadNamePrefix("discodeit-async-");
    executor.setTaskDecorator(new ContextCopyingTaskDecorator());
    executor.initialize();
    return executor;
  }

  @Bean("eventTaskExecutor")
  public TaskExecutor eventTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("discodeit-event-async-");
    executor.setTaskDecorator(new ContextCopyingTaskDecorator());
    executor.initialize();
    return executor;
  }

  static class ContextCopyingTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
      Map<String, String> contextMap = MDC.getCopyOfContextMap();
      SecurityContext securityContext = SecurityContextHolder.getContext();
      return () -> {
        Map<String, String> previous = MDC.getCopyOfContextMap();
        SecurityContext previousSecurityContext = SecurityContextHolder.getContext();
        try {
          if (contextMap != null) {
            MDC.setContextMap(contextMap);
          }
          SecurityContextHolder.setContext(securityContext);
          runnable.run();
        } finally {
          if (previous != null) {
            MDC.setContextMap(previous);
          } else {
            MDC.clear();
          }
          SecurityContextHolder.setContext(previousSecurityContext);
        }
      };
    }
  }
}

