package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 채널 저장소 인터페이스
 * 채널 데이터에 대한 CRUD 작업을 정의합니다.
 */
public interface ChannelRepository {
    /**
     * 채널을 저장합니다.
     *
     * @param channel 저장할 채널
     * @return 저장된 채널
     */
    Channel save(Channel channel);

    /**
     * ID로 채널을 조회합니다.
     *
     * @param id 채널 ID
     * @return 조회된 채널 (Optional)
     */
    Optional<Channel> findById(UUID id);

    /**
     * 모든 채널을 조회합니다.
     *
     * @return 모든 채널 목록
     */
    List<Channel> findAll();

    /**
     * ID로 채널을 삭제합니다.
     *
     * @param id 삭제할 채널 ID
     */
    void deleteById(UUID id);

    /**
     * ID로 채널의 존재 여부를 확인합니다.
     *
     * @param id 채널 ID
     * @return 존재 여부
     */
    boolean existsById(UUID id);
}
