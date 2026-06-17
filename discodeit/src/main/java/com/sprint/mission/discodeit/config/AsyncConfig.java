package com.sprint.mission.discodeit.config;

import java.util.Map;
import java.util.concurrent.Executor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableAsync // 비동기 기능 활성화
public class AsyncConfig {

  @Bean(name = "taskExecutor")
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);       // 기본 스레드 수
    executor.setMaxPoolSize(10);       // 최대 스레드 수
    executor.setQueueCapacity(100);    // 대기 큐 크기
    executor.setThreadNamePrefix("AsyncExecutor-");
    executor.setTaskDecorator(new ContextCopyingDecorator()); // 컨텍스트 전파 데코레이터 설정
    executor.initialize();
    return executor;
  }

  @Bean(name = "eventTaskExecutor")
  public Executor eventTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("EventAsyncExecutor-");
    executor.setTaskDecorator(new ContextCopyingDecorator());
    executor.initialize();
    return executor;
  }

  /**
   * 메인 스레드의 MDC와 SecurityContext를 비동기 스레드로 복사하는 데코레이터
   */
  static class ContextCopyingDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
      // [메인 스레드] 현재 스레드의 MDC context와 SecurityContext를 가져옴
      Map<String, String> contextMap = MDC.getCopyOfContextMap();
      SecurityContext securityContext = SecurityContextHolder.getContext();

      return () -> {
        try {
          // [비동기 스레드] 가져온 컨텍스트를 비동기 스레드에 세팅
          if (contextMap != null) {
            MDC.setContextMap(contextMap);
          }
          SecurityContextHolder.setContext(securityContext);

          // 실제 작업 수행
          runnable.run();
        } finally {
          // 작업 완료 후 비동기 스레드 컨텍스트 정리 (스레드 풀 오염 방지)
          SecurityContextHolder.clearContext();
          MDC.clear();
        }
      };
    }
  }
}