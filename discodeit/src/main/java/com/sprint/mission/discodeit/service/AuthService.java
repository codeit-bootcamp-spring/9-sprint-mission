package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;

/**
 * 인증 서비스 인터페이스
 * 사용자 로그인 및 인증 관련 기능을 정의합니다.
 */
public interface AuthService {
    /**
     * 사용자 로그인을 처리합니다.
     *
     * @param request 로그인 요청 정보 (사용자명, 비밀번호)
     * @return 로그인한 사용자 정보
     */
    UserResponse login(LoginRequest request);
}