package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 읽기 상태 저장소 인터페이스
 * 사용자의 채널별 메시지 읽기 상태에 대한 CRUD 작업을 정의합니다.
 */
public interface ReadStatusRepository {
    /**
     * 읽기 상태를 저장합니다.
     *
     * @param readStatus 저장할 읽기 상태
     * @return 저장된 읽기 상태
     */
    ReadStatus save(ReadStatus readStatus);

    /**
     * ID로 읽기 상태를 조회합니다.
     *
     * @param id 읽기 상태 ID
     * @return 조회된 읽기 상태 (Optional)
     */
    Optional<ReadStatus> findById(UUID id);

    /**
     * 모든 읽기 상태를 조회합니다.
     *
     * @return 모든 읽기 상태 목록
     */
    List<ReadStatus> findAll();

    /**
     * 특정 사용자의 모든 읽기 상태를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자의 읽기 상태 목록
     */
    List<ReadStatus> findAllByUserId(UUID userId);

    /**
     * 특정 채널의 모든 읽기 상태를 조회합니다.
     *
     * @param channelId 채널 ID
     * @return 해당 채널의 읽기 상태 목록
     */
    List<ReadStatus> findAllByChannelId(UUID channelId);

    /**
     * 특정 사용자의 특정 채널에 대한 읽기 상태를 조회합니다.
     *
     * @param userId 사용자 ID
     * @param channelId 채널 ID
     * @return 조회된 읽기 상태 (Optional)
     */
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    /**
     * ID로 읽기 상태를 삭제합니다.
     *
     * @param id 삭제할 읽기 상태 ID
     */
    void deleteById(UUID id);

    /**
     * 특정 채널의 모든 읽기 상태를 일괄 삭제합니다.
     *
     * @param channelId 채널 ID
     */
    void deleteAllByChannelId(UUID channelId);

    /**
     * ID로 읽기 상태의 존재 여부를 확인합니다.
     *
     * @param id 읽기 상태 ID
     * @return 존재 여부
     */
    boolean existsById(UUID id);
}