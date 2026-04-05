package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

  private static final String REQUEST_ID = "requestId";
  private static final String METHOD = "httpMethod";
  private static final String URI = "requestUri";
  private static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";

  @Override
  public boolean preHandle(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler) {
    String requestId = UUID.randomUUID().toString();

    MDC.put(REQUEST_ID, requestId);
    MDC.put(METHOD, request.getMethod());
    MDC.put(URI, request.getRequestURI());

    response.setHeader(REQUEST_ID_HEADER, requestId);

    log.info("Request started");
    return true;
  }

  @Override
  public void afterCompletion(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler,
      Exception ex) {
    try {
      if (ex != null) {
        log.error("Request failed", ex);
      } else {
        log.info("Request completed with status={}", response.getStatus());
      }
    } finally {
      MDC.clear();
    }
  }
}
