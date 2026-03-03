package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 메시지 저장소 인터페이스
 * 메시지 데이터에 대한 CRUD 작업을 정의합니다.
 */
public interface MessageRepository {
    /**
     * 메시지를 저장합니다.
     *
     * @param message 저장할 메시지
     * @return 저장된 메시지
     */
    Message save(Message message);

    /**
     * ID로 메시지를 조회합니다.
     *
     * @param id 메시지 ID
     * @return 조회된 메시지 (Optional)
     */
    Optional<Message> findById(UUID id);

    /**
     * 모든 메시지를 조회합니다.
     *
     * @return 모든 메시지 목록
     */
    List<Message> findAll();

    /**
     * 특정 채널의 모든 메시지를 조회합니다.
     *
     * @param channelId 채널 ID
     * @return 해당 채널의 메시지 목록
     */
    List<Message> findAllByChannelId(UUID channelId);

    /**
     * ID로 메시지를 삭제합니다.
     *
     * @param id 삭제할 메시지 ID
     */
    void deleteById(UUID id);

    /**
     * 특정 채널의 모든 메시지를 일괄 삭제합니다.
     *
     * @param channelId 채널 ID
     */
    void deleteAllByChannelId(UUID channelId);

    /**
     * ID로 메시지의 존재 여부를 확인합니다.
     *
     * @param id 메시지 ID
     * @return 존재 여부
     */
    boolean existsById(UUID id);
}
