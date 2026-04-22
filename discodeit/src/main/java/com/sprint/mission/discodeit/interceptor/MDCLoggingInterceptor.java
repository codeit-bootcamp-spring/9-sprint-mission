package com.sprint.mission.discodeit.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.UUID;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    String requestId = UUID.randomUUID().toString();
    MDC.put("requestId", requestId);
    MDC.put("method", request.getMethod());
    MDC.put("url", request.getRequestURI());

    response.setHeader("Discodeit-Request-ID", requestId);
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {
    MDC.clear();
  }
}