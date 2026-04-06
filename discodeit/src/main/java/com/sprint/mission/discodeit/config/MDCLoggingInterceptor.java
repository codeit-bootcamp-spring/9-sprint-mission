package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    // 1. UUID 생성 (예시처럼 앞 8자리만 사용하여 가독성 확보)
    String requestId = UUID.randomUUID().toString().split("-")[0];

    // 2. MDC에 값 세팅
    MDC.put("request_id", requestId);
    MDC.put("request_method", request.getMethod());
    MDC.put("request_url", request.getRequestURI());

    // 3. 응답 헤더에 추가
    response.setHeader("Discodeit-Request-ID", requestId);

    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {
    // 4. 요청 처리가 끝나면 반드시 MDC를 비워주어 스레드 풀 환경에서 메모리 누수 및 데이터 꼬임 방지
    MDC.clear();
  }
}
