package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserView;


public interface AuthService {
    /**
     * - displayName / password로 로그인
     * - 일치하는 유저가 있으면: 유저 정보(UserView) 반환
     * - 없으면: 예외 발생(구현체에서 처리)
     */
    UserView login(LoginRequest request);
}
