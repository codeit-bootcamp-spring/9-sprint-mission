package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import java.util.List;
import java.util.UUID;

/**
 * 사용자 서비스 인터페이스
 * 사용자 생성, 조회, 수정, 삭제 및 프로필 이미지 관리 기능을 정의합니다.
 */
public interface UserService {
    /**
     * 사용자를 생성합니다 (프로필 이미지 포함 가능).
     *
     * @param request 사용자 생성 요청
     * @param profileRequest 프로필 이미지 생성 요청 (null 가능)
     * @return 생성된 사용자 정보
     */
    UserResponse create(UserCreateRequest request, BinaryContentCreateRequest profileRequest);

    /**
     * 모든 사용자를 UserDto 형식으로 조회합니다.
     *
     * @return 모든 사용자 목록 (UserDto 형식)
     */
    List<UserDto> findAllAsDto();

    /**
     * 사용자 정보를 업데이트합니다 (프로필 이미지 변경 가능).
     *
     * @param id 사용자 ID
     * @param request 사용자 업데이트 요청
     * @param profileRequest 프로필 이미지 업데이트 요청 (null 가능)
     * @return 업데이트된 사용자 정보
     */
    UserResponse update(UUID id, UserUpdateRequest request, BinaryContentCreateRequest profileRequest);

    /**
     * 사용자를 삭제합니다.
     *
     * @param id 삭제할 사용자 ID
     */
    void delete(UUID id);
}
