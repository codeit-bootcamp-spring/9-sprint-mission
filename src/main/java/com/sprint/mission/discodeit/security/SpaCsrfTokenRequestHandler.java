package com.sprint.mission.discodeit.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

  // 헤더 방식 (SPA) → XOR 인코딩 없이 raw 토큰 그대로 비교
  private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();

  // 폼 방식 (SSR) → BREACH 공격 방어를 위해 XOR 인코딩 적용
  private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      Supplier<CsrfToken> csrfToken) {
    // XOR 핸들러로 처리 → 응답 바디에 토큰 렌더링 시 BREACH 방어
    this.xor.handle(request, response, csrfToken);

    // csrfToken.get() 호출 → 지연 로딩된 토큰을 강제 로드해 쿠키에 즉시 저장
    csrfToken.get();
  }

  @Override
  public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
    String headerValue = request.getHeader(csrfToken.getHeaderName());

    // X-XSRF-TOKEN 헤더가 있으면 → SPA 방식 (plain: raw 토큰 그대로 비교)
    // 헤더가 없으면 → SSR 폼 방식 (_csrf 파라미터, xor: XOR 디코딩 후 비교)
    return (StringUtils.hasText(headerValue) ? this.plain : this.xor)
        .resolveCsrfTokenValue(request, csrfToken);
  }
}
