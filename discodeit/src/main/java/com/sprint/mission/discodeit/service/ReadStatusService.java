package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;

/**
 * 읽기 상태 서비스 인터페이스
 * 사용자의 채널별 메시지 읽기 상태를 관리하는 기능을 정의합니다.
 */
public interface ReadStatusService {
    /**
     * 읽기 상태를 생성합니다.
     *
     * @param request 읽기 상태 생성 요청
     * @return 생성된 읽기 상태
     */
    ReadStatus create(ReadStatusCreateRequest request);

    /**
     * ID로 읽기 상태를 조회합니다.
     *
     * @param id 읽기 상태 ID
     * @return 조회된 읽기 상태
     */
    ReadStatus find(UUID id);

    /**
     * 특정 사용자의 모든 읽기 상태를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 읽기 상태 목록
     */
    List<ReadStatus> findAllByUserId(UUID userId);

    /**
     * 읽기 상태를 업데이트합니다.
     *
     * @param id 읽기 상태 ID
     * @param request 읽기 상태 업데이트 요청
     * @return 업데이트된 읽기 상태
     */
    ReadStatus update(UUID id, ReadStatusUpdateRequest request);

    /**
     * 읽기 상태를 삭제합니다.
     *
     * @param id 삭제할 읽기 상태 ID
     */
    void delete(UUID id);
}