package com.sprint.mission.discodeit.config;

import java.util.Map;
import java.util.concurrent.Executor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@EnableAsync
@EnableRetry
@Configuration
public class AsyncConfig {

  @Bean(name = "applicationTaskExecutor")
  public Executor applicationTaskExecutor(TaskDecorator contextPropagatingTaskDecorator) {
    return taskExecutor(contextPropagatingTaskDecorator, "discodeit-async-");
  }

  @Bean(name = "eventTaskExecutor")
  public Executor eventTaskExecutor(TaskDecorator contextPropagatingTaskDecorator) {
    return taskExecutor(contextPropagatingTaskDecorator, "discodeit-event-");
  }

  private Executor taskExecutor(TaskDecorator contextPropagatingTaskDecorator,
      String threadNamePrefix) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix(threadNamePrefix);
    executor.setTaskDecorator(contextPropagatingTaskDecorator);
    executor.initialize();
    return executor;
  }

  @Bean
  public TaskDecorator contextPropagatingTaskDecorator() {
    return task -> {
      Map<String, String> mdcContext = MDC.getCopyOfContextMap();
      SecurityContext securityContext = SecurityContextHolder.getContext();

      return () -> {
        Map<String, String> previousMdcContext = MDC.getCopyOfContextMap();
        SecurityContext previousSecurityContext = SecurityContextHolder.getContext();
        try {
          if (mdcContext == null) {
            MDC.clear();
          } else {
            MDC.setContextMap(mdcContext);
          }
          SecurityContextHolder.setContext(securityContext);
          task.run();
        } finally {
          if (previousMdcContext == null) {
            MDC.clear();
          } else {
            MDC.setContextMap(previousMdcContext);
          }
          SecurityContextHolder.setContext(previousSecurityContext);
        }
      };
    };
  }
}
