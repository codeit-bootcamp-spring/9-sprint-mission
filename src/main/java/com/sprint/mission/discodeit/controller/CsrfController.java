package com.sprint.mission.discodeit.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// CsrfController.java
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class CsrfController {

  /**
   * CSRF 토큰 발급
   * CsrfToken을 메서드 인자로 선언 → HandlerMethodArgumentResolver가 자동 주입
   * csrfToken.getToken() 명시적 호출 → 지연 로딩 토큰 강제 초기화 → Set-Cookie 응답 헤더 세팅
   */
  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken(); // ← 명시적 호출로 토큰 강제 초기화
    log.debug("CSRF 토큰 요청: {}", tokenValue);

    return ResponseEntity.noContent().build(); // 204 No Content
  }
}
