package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.UUID;

/**
 * 사용자 상태 서비스 인터페이스
 * 사용자의 온라인 상태 및 활동 정보를 관리하는 기능을 정의합니다.
 */
public interface UserStatusService {
    /**
     * 사용자 상태를 생성합니다.
     *
     * @param request 사용자 상태 생성 요청
     * @return 생성된 사용자 상태
     */
    UserStatus create(UserStatusCreateRequest request);

    /**
     * ID로 사용자 상태를 조회합니다.
     *
     * @param id 사용자 상태 ID
     * @return 조회된 사용자 상태
     */
    UserStatus find(UUID id);

    /**
     * 사용자 ID로 사용자 상태를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 조회된 사용자 상태
     */
    UserStatus findByUserId(UUID userId);

    /**
     * 모든 사용자 상태를 조회합니다.
     *
     * @return 모든 사용자 상태 목록
     */
    List<UserStatus> findAll();

    /**
     * 사용자 상태를 업데이트합니다.
     *
     * @param id 사용자 상태 ID
     * @param request 사용자 상태 업데이트 요청
     * @return 업데이트된 사용자 상태
     */
    UserStatus update(UUID id, UserStatusUpdateRequest request);

    /**
     * 사용자 ID로 사용자 상태를 업데이트합니다.
     *
     * @param userId 사용자 ID
     * @param request 사용자 상태 업데이트 요청
     * @return 업데이트된 사용자 상태
     */
    UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request);

    /**
     * 사용자 상태를 삭제합니다.
     *
     * @param id 삭제할 사용자 상태 ID
     */
    void delete(UUID id);
}