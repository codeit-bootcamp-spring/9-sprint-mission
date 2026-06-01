package com.sprint.mission.discodeit.config.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class DevToolsIgnoreFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // 초기화 필요 시 작성
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    String uri = request.getRequestURI();

    // 크롬 개발자 도구 자동 요청이나 인증 경로 무시
    if (uri.contains(".well-known") || uri.contains("com.chrome.devtools.json")) {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getWriter()
          .write("{\"status\": \"ok\", \"message\": \"Chrome DevTools auto request ignored\"}");
      return;
    }

    // 나머지는 정상 흐름대로 진행
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    // 리소스 해제 필요 시 작성
  }
}
