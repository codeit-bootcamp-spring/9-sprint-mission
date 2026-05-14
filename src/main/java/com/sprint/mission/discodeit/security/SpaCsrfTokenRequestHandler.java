package com.sprint.mission.discodeit.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.function.Supplier;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

import org.springframework.util.StringUtils;

public final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

  private final CsrfTokenRequestHandler plain =
      new CsrfTokenRequestAttributeHandler();

  private final CsrfTokenRequestHandler xor =
      new XorCsrfTokenRequestAttributeHandler();

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      Supplier<CsrfToken> csrfToken
  ) {

    // BREACH 공격 방어용 XOR 처리
    this.xor.handle(request, response, csrfToken);

    // csrfToken 강제 생성
    csrfToken.get();
  }

  @Override
  public String resolveCsrfTokenValue(
      HttpServletRequest request,
      CsrfToken csrfToken
  ) {

    String headerValue =
        request.getHeader(csrfToken.getHeaderName());

    return (StringUtils.hasText(headerValue)
        ? this.plain
        : this.xor)
        .resolveCsrfTokenValue(request, csrfToken);
  }

}
