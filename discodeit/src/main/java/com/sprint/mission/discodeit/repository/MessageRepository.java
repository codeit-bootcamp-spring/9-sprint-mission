package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    // Page 대신 Slice 반환으로 변경하여 성능 최적화 (Count 쿼리 방지)
    Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);
    void deleteAllByChannelId(UUID channelId);
}