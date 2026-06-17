package com.sprint.mission.discodeit.config;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MdcSecurityContextTaskDecorator implements TaskDecorator {

  @Override
  public Runnable decorate(Runnable runnable) {
    Map<String, String> mdcContext = MDC.getCopyOfContextMap();
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return () -> {
      try {
        if (mdcContext != null) {
          MDC.setContextMap(mdcContext);
        }
        SecurityContextHolder.setContext(securityContext);
        runnable.run();
      } finally {
        MDC.clear();
        SecurityContextHolder.clearContext();
      }
    };
  }
}
