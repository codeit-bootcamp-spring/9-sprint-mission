package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 상태 저장소 인터페이스
 * 사용자의 온라인 상태 및 활동 정보에 대한 CRUD 작업을 정의합니다.
 */
public interface UserStatusRepository {
    /**
     * 사용자 상태를 저장합니다.
     *
     * @param userStatus 저장할 사용자 상태
     * @return 저장된 사용자 상태
     */
    UserStatus save(UserStatus userStatus);

    /**
     * ID로 사용자 상태를 조회합니다.
     *
     * @param id 사용자 상태 ID
     * @return 조회된 사용자 상태 (Optional)
     */
    Optional<UserStatus> findById(UUID id);

    /**
     * 사용자 ID로 사용자 상태를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 조회된 사용자 상태 (Optional)
     */
    Optional<UserStatus> findByUserId(UUID userId);

    /**
     * 모든 사용자 상태를 조회합니다.
     *
     * @return 모든 사용자 상태 목록
     */
    List<UserStatus> findAll();

    /**
     * ID로 사용자 상태를 삭제합니다.
     *
     * @param id 삭제할 사용자 상태 ID
     */
    void deleteById(UUID id);

    /**
     * 사용자 ID로 사용자 상태를 삭제합니다.
     *
     * @param userId 사용자 ID
     */
    void deleteByUserId(UUID userId);

    /**
     * ID로 사용자 상태의 존재 여부를 확인합니다.
     *
     * @param id 사용자 상태 ID
     * @return 존재 여부
     */
    boolean existsById(UUID id);

    /**
     * 사용자 ID로 사용자 상태의 존재 여부를 확인합니다.
     *
     * @param userId 사용자 ID
     * @return 존재 여부
     */
    boolean existsByUserId(UUID userId);
}