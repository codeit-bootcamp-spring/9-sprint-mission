package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 저장소 인터페이스
 * 사용자 데이터에 대한 CRUD 작업 및 인증 관련 조회 기능을 정의합니다.
 */
public interface UserRepository {
    /**
     * 사용자를 저장합니다.
     *
     * @param user 저장할 사용자
     * @return 저장된 사용자
     */
    User save(User user);

    /**
     * ID로 사용자를 조회합니다.
     *
     * @param id 사용자 ID
     * @return 조회된 사용자 (Optional)
     */
    Optional<User> findById(UUID id);

    /**
     * 사용자명으로 사용자를 조회합니다 (로그인 시 사용).
     *
     * @param username 사용자명
     * @return 조회된 사용자 (Optional)
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 사용자를 조회합니다 (이메일 중복 체크 시 사용).
     *
     * @param email 이메일 주소
     * @return 조회된 사용자 (Optional)
     */
    Optional<User> findByEmail(String email);

    /**
     * 모든 사용자를 조회합니다.
     *
     * @return 모든 사용자 목록
     */
    List<User> findAll();

    /**
     * ID로 사용자를 삭제합니다.
     *
     * @param id 삭제할 사용자 ID
     */
    void deleteById(UUID id);

    /**
     * ID로 사용자의 존재 여부를 확인합니다.
     *
     * @param id 사용자 ID
     * @return 존재 여부
     */
    boolean existsById(UUID id);

    /**
     * 사용자명으로 사용자의 존재 여부를 확인합니다 (username 중복 체크).
     *
     * @param username 사용자명
     * @return 존재 여부
     */
    boolean existsByUsername(String username);

    /**
     * 이메일로 사용자의 존재 여부를 확인합니다 (email 중복 체크).
     *
     * @param email 이메일 주소
     * @return 존재 여부
     */
    boolean existsByEmail(String email);
}