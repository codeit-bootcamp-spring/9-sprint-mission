package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

  private static final String REQUEST_ID = "requestId";
  private static final String METHOD = "method";
  private static final String URL = "url";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

    String requestId = UUID.randomUUID().toString();

    MDC.put(REQUEST_ID, requestId);
    MDC.put(METHOD, request.getMethod());
    MDC.put(URL, request.getRequestURI());

    response.setHeader("Discodeit-Request-ID", requestId);

    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    MDC.clear();
  }
}