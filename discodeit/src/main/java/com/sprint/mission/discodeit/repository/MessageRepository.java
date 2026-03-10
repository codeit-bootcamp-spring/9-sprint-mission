package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    // List 반환을 Page 반환으로 변경하고 Pageable 파라미터 추가
    Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);

    void deleteAllByChannelId(UUID channelId);
}