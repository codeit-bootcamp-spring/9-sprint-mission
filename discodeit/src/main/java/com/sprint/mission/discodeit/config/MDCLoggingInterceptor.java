package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

  private static final String REQUEST_ID_KEY = "requestId";
  private static final String REQUEST_METHOD_KEY = "method";
  private static final String REQUEST_URL_KEY = "url";
  private static final String HEADER_NAME = "Discodeit-Request-ID";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String requestId = UUID.randomUUID().toString();
    MDC.put(REQUEST_ID_KEY, requestId);
    MDC.put(REQUEST_METHOD_KEY, request.getMethod());
    MDC.put(REQUEST_URL_KEY, request.getRequestURI());

    response.setHeader(HEADER_NAME, requestId);

    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    MDC.clear();
  }
}
