package com.sprint.mission.discodeit.config;

import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.core.task.TaskDecorator;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig {

  /**
   * MDC의 Request ID와 SecurityContext 인증 정보를
   * 비동기 스레드에도 전파하는 TaskDecorator.
   */
  @Bean
  public TaskDecorator contextCopyDecorator() {
    return runnable -> {
      // 호출 스레드에서 컨텍스트 캡처
      String requestId = MDC.get("requestId");
      SecurityContext securityContext = SecurityContextHolder.getContext();

      return () -> {
        try {
          // 비동기 스레드에 컨텍스트 복원
          if (requestId != null) {
            MDC.put("requestId", requestId);
          }
          SecurityContextHolder.setContext(securityContext);

          runnable.run();
        } finally {
          // 스레드 풀 재사용 시 컨텍스트 오염 방지
          MDC.remove("requestId");
          SecurityContextHolder.clearContext();
        }
      };
    };
  }

  /**
   * 이벤트 리스너 전용 스레드 풀.
   * Spring이 @Async 실행 시 이 Executor를 사용합니다.
   */
  @Bean(name = "notificationExecutor")
  public Executor notificationExecutor(TaskDecorator contextCopyDecorator) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);          // 기본 스레드 수
    executor.setMaxPoolSize(10);          // 최대 스레드 수
    executor.setQueueCapacity(500);       // 대기 큐 용량
    executor.setThreadNamePrefix("notification-"); // 스레드 이름 (로그 식별용)
    executor.setTaskDecorator(contextCopyDecorator);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(30);
    executor.initialize();
    return executor;
  }
}
