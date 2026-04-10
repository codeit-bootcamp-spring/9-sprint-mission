package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

  public static final String HEADER_REQUEST_ID = "Discodeit-Request-ID";
  public static final String MDC_REQUEST_ID = "requestId";
  public static final String MDC_REQUEST_METHOD = "requestMethod";
  public static final String MDC_REQUEST_URL = "requestUrl";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String requestId = UUID.randomUUID().toString();
    String requestUrl = resolveRequestUrl(request);

    MDC.put(MDC_REQUEST_ID, requestId);
    MDC.put(MDC_REQUEST_METHOD, request.getMethod());
    MDC.put(MDC_REQUEST_URL, requestUrl);

    response.setHeader(HEADER_REQUEST_ID, requestId);
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
      Exception ex) {
    MDC.clear();
  }

  private String resolveRequestUrl(HttpServletRequest request) {
    String uri = request.getRequestURI();
    String queryString = request.getQueryString();
    if (queryString == null || queryString.isBlank()) {
      return uri;
    }
    return uri + "?" + queryString;
  }
}

