package com.sprint.mission.discodeit.secure.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

// SPA(Single Page Application) 환경에 맞는 CSRF 토큰 핸들러
// 헤더로 토큰을 받을 때는 plain 방식, form 파라미터로 받을 때는 xor 방식 사용
public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

  // 헤더 방식: X-XSRF-TOKEN 헤더에서 토큰을 그대로 읽음
  private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
  // form 방식: XOR 변환된 토큰을 사용 (BREACH 공격 방어)
  private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      Supplier<CsrfToken> csrfToken) {
    // 응답 시 항상 xor 방식으로 처리 (BREACH 보호)
    this.xor.handle(request, response, csrfToken);
    // 토큰을 실제로 로드해서 쿠키에 값이 담기도록 강제 호출
    csrfToken.get();
  }

  @Override
  public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
    String headerValue = request.getHeader(csrfToken.getHeaderName());
    // 헤더에 토큰이 있으면 plain(SPA 방식), 없으면 xor(form 방식)으로 검증
    return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(request,
        csrfToken);
  }
}

